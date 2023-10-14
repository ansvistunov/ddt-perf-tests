package com.asw.billing.front.grpc;

import com.asw.billing.service.BillingService;
import com.asw.billing.storage.BillingStorage;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@AllArgsConstructor
public class BillingGrpcImpl extends BillingServiceStreamGrpc.BillingServiceStreamImplBase{
    BillingService billingService;

    public void addNewCard(com.asw.billing.front.grpc.AddNewCardRequest request,
                           io.grpc.stub.StreamObserver<com.asw.billing.front.grpc.AddNewCardResponse> responseObserver) {
        BillingStorage.Card card = billingService.createCard(request.getPersonname());
        responseObserver.onNext(AddNewCardResponse.newBuilder()
                        .setCard(card.id())
                        .setDatecreaded(card.createdAt().toString())
                        .setPersonname(card.persona())
                        .setBalance(card.ballance())
                        .build());
        responseObserver.onCompleted();
    }

    /**
     */
    public io.grpc.stub.StreamObserver<com.asw.billing.front.grpc.MoneyRequest> processOperation(
            io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
        return new StreamObserver<MoneyRequest>() {
            long count = 0;
            long startTime = System.nanoTime();
            @Override
            public void onNext(MoneyRequest moneyRequest) {
                billingService.processOperation(new BillingStorage.CardOperation(moneyRequest.getCard(),
                        LocalDateTime.parse(moneyRequest.getDatetime()), moneyRequest.getMoney()));
                //count++;
            }
            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
            }
            @Override
            public void onCompleted() {
                //System.out.println(String.format("Stream complete. elements count=%s, seconds=%s",count, NANOSECONDS.toSeconds(System.nanoTime() - startTime)));
                //System.out.println("cards="+cards);
                responseObserver.onNext(Empty.newBuilder().build());
                responseObserver.onCompleted();
            }
        };

    }

    /**
     */
    public void getCardBalance(com.asw.billing.front.grpc.GetCardBalanceRequest request,
                               io.grpc.stub.StreamObserver<com.asw.billing.front.grpc.GetCardBalanceResponse> responseObserver) {
        BillingStorage.Card card = billingService.getCard(request.getCard());
        responseObserver.onNext(GetCardBalanceResponse.newBuilder()
                .setBalance(card.ballance())
                .build());
        responseObserver.onCompleted();
    }


}
