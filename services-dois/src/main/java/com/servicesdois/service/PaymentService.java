package com.servicesdois.service;

import com.servicesdois.dto.Event;
import com.servicesdois.dto.History;
import com.servicesdois.enums.EPaymentStatus;
import com.servicesdois.kafka.Producer;
import com.servicesdois.models.Payment;
import com.servicesdois.repository.PaymentRepository;
import com.servicesdois.utils.JsonUtil;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

import static com.servicesdois.enums.EPaymentStatus.SUCCESS;
import static com.servicesdois.enums.EStatus.FAIL;
import static com.servicesdois.enums.EStatus.ROLLBACK_PENDING;

@Singleton
public class PaymentService {
    private static final Logger LOG = LoggerFactory.getLogger(PaymentService.class);

    private static final Double MIN_AMOUNT_VALUE = 0.1;
    @Inject
    private JsonUtil jsonUtil;
    @Inject
    private Producer producer;
    @Inject
    private PaymentRepository paymentRepository;

    public void checkCurrentValidation(Event event){
        if(paymentRepository.existsById(event.getPayload().getId())){
            throw new RuntimeException("There's another Id for this validation.");
        }
    }

    private double calculateAmount(Event event){
        return event.getPayload().getProducts().stream() .map(product -> product.getPrice())
                .reduce(0.0, Double::sum);
    }

    public void createPendingPayment(Event event){
        double totalAmount = calculateAmount(event);
        Payment payment = new Payment();
        payment.setId(event.getPayload().getId());
        payment.setTotalAmount(totalAmount);
        paymentRepository.save(payment);
    }

    private void validateAmount(double amount){
        if(amount < MIN_AMOUNT_VALUE){
            throw new RuntimeException("The minimum amount available is ".concat(MIN_AMOUNT_VALUE.toString()));
        }
    }
    private void changePaymentToSuccess(Payment payment){
        payment.setStatus(SUCCESS);
        paymentRepository.update(payment);
    }

    private void addHistory(Event event, String message){
        History history = new History();
        history.setSource(event.getSource());
        history.setStatus(event.getStatus());
        history.setMessage(message);
        history.setCreatedAt(LocalDateTime.now());
        event.addToHistory(history);
    }
    private void handleFailCurrentNotExecuted(Event event, String message){
        event.setStatus(String.valueOf(ROLLBACK_PENDING));
        event.setSource("PAYMENT_SERVICE");
        addHistory(event, "Fail to realized payment: "
                .concat(message));
    }

    private Payment findById(Event event){
        return paymentRepository.findById(event.getPayload().getId()).orElseThrow(() ->
                new RuntimeException("Payment not found by Id. "));
    }
    private void changePaymentStatusToRefund(Event event){
        Payment payment = findById(event);
        payment.setStatus(EPaymentStatus.REFUND);
        paymentRepository.update(payment);
    }
    public void realizedRefund(Event event){
        event.setStatus(String.valueOf(FAIL)); event.setSource("PAYMENT_SERVICE");
        try{
            changePaymentStatusToRefund(event); addHistory(event, "Rollback executed for payment! ");
        }catch (Exception ex){
            addHistory(event, "Rollback not executed for payment! "
                    .concat(ex.getMessage()));
        }
        producer.sendEvent(jsonUtil.toJson(event));
    }
    private void handleSuccess(Event event){
        event.setStatus(String.valueOf(SUCCESS));
        event.setSource("PAYMENT_SERVICE");
        addHistory(event, "Payment realized successfully");
    }
    public void realizedPayment(Event event){
        try{ checkCurrentValidation(event);
            createPendingPayment(event);
            Payment payment = findById(event);
            validateAmount(payment.getTotalAmount());
            changePaymentToSuccess(payment);
            handleSuccess(event);
        }catch (Exception ex) {
            LOG.error("Error trying to make payment: " , ex);
            handleFailCurrentNotExecuted(event, ex.getMessage());
        }
        producer.sendEvent(jsonUtil.toJson(event));
    }







}
