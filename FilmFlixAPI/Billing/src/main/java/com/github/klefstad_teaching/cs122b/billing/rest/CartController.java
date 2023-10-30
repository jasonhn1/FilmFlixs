package com.github.klefstad_teaching.cs122b.billing.rest;


import com.github.klefstad_teaching.cs122b.billing.model.Item;
import com.github.klefstad_teaching.cs122b.billing.repo.BillingRepo;
import com.github.klefstad_teaching.cs122b.billing.request.CartModelRequest;
import com.github.klefstad_teaching.cs122b.billing.response.CartModelResponse;
import com.github.klefstad_teaching.cs122b.billing.response.CartRetrieveResponse;
import com.github.klefstad_teaching.cs122b.billing.util.Validate;
import com.github.klefstad_teaching.cs122b.core.result.BillingResults;
import com.github.klefstad_teaching.cs122b.core.security.JWTManager;
import com.nimbusds.jwt.SignedJWT;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;

@RestController
public class CartController
{
    private final BillingRepo repo;
    private final Validate    validate;

    @Autowired
    public CartController(BillingRepo repo, Validate validate)
    {
        this.repo = repo;
        this.validate = validate;
    }

    @PostMapping("/cart/insert")
    public ResponseEntity<CartModelResponse> cartInsert(
            @AuthenticationPrincipal SignedJWT user,
            @RequestBody CartModelRequest request
    ){
        Integer quantity = request.getQuantity();

        if(quantity <=0){
            return new CartModelResponse()
                    .setResult(BillingResults.INVALID_QUANTITY)
                    .toResponse();
        }


        if( quantity > 10){
            return new CartModelResponse()
                    .setResult(BillingResults.MAX_QUANTITY)
                    .toResponse();
        }

        Integer userId = -1;
        // Get the user Id from JWT
        try{
            userId = user.getJWTClaimsSet().getIntegerClaim(JWTManager.CLAIM_ID);

        }catch (ParseException e){
            System.out.println("Cannot Parse Claimset");
        }

        Boolean alreadyInCart = repo.alreadyInCart(userId,request.getMovieId());

        // This means the item already exists in tchar
        if(alreadyInCart){
            return new CartModelResponse()
                    .setResult(BillingResults.CART_ITEM_EXISTS)
                    .toResponse();

        }else{
            repo.insertIntoCart(userId, request.getMovieId(), request.getQuantity());
            return new CartModelResponse()
                    .setResult(BillingResults.CART_ITEM_INSERTED)
                    .toResponse();

        }



    }

    @PostMapping("/cart/update")
    public ResponseEntity<CartModelResponse> cartUpdate(
            @AuthenticationPrincipal SignedJWT user,
            @RequestBody CartModelRequest request
    ) {

        Integer quantity = request.getQuantity();

        if(quantity <=0){
            return new CartModelResponse()
                    .setResult(BillingResults.INVALID_QUANTITY)
                    .toResponse();
        }

        if( quantity > 10){
            return new CartModelResponse()
                    .setResult(BillingResults.MAX_QUANTITY)
                    .toResponse();
        }

        Integer userId = -1;
        // Get the user Id from JWT
        try{
            userId = user.getJWTClaimsSet().getIntegerClaim(JWTManager.CLAIM_ID);

        }catch (ParseException e){
            System.out.println("Cannot Parse Claimset");
        }

       return repo.updateCart(userId,request.getMovieId(),request.getQuantity());

    }




    @PostMapping("/cart/delete/{movieId}")
    public ResponseEntity<CartModelResponse> cartDelete(
            @AuthenticationPrincipal SignedJWT user,
            @PathVariable Long movieId
    ) {
        Integer userId = -1;
        // Get the user Id from JWT
        try{
            userId = user.getJWTClaimsSet().getIntegerClaim(JWTManager.CLAIM_ID);

        }catch (ParseException e){
            System.out.println("Cannot Parse Claimset");
        }
        Boolean alreadyInCart = repo.alreadyInCart(userId,movieId);

        if(alreadyInCart) {

            repo.deleteItem(userId,movieId);

            return new CartModelResponse()
                    .setResult(BillingResults.CART_ITEM_DELETED)
                    .toResponse();
        }else {// if movie not in cart
            return new CartModelResponse()
                    .setResult(BillingResults.CART_ITEM_DOES_NOT_EXIST)
                    .toResponse();
        }
    }

    @GetMapping("/cart/retrieve")
    public ResponseEntity<CartRetrieveResponse> cartRetrieve(
            @AuthenticationPrincipal SignedJWT user
    ) {
        Integer userId = -1;
        // Get the user Id from JWT
        Boolean premium = false;
        try{
            userId = user.getJWTClaimsSet().getIntegerClaim(JWTManager.CLAIM_ID);
            List<String> roles = user.getJWTClaimsSet().getStringListClaim(JWTManager.CLAIM_ROLES);
            //Looking for premium users
            for(String role:roles){
                if(role.equals("PREMIUM")){
                    premium = true;
                }
            }

        }catch (ParseException e){
            System.out.println("Cannot Parse Claimset");
        }


        List<Item> items = repo.retriveItem(userId,premium);


        for(Item item: items){


            BigDecimal price = item.getUnitPrice();

        }

        BigDecimal hundreds = BigDecimal.valueOf(1).setScale(2);



        return new CartRetrieveResponse()
                    .setItem(items)
                    .setTotal(hundreds)
                    .toResponse();


    }







    @PostMapping("/cart/clear")
    public ResponseEntity<CartModelResponse> cartClear(
            @AuthenticationPrincipal SignedJWT user){

        Integer userId = -1;
        // Get the user Id from JWT
        Boolean premium = false;
        try{
            userId = user.getJWTClaimsSet().getIntegerClaim(JWTManager.CLAIM_ID);
        }catch (ParseException e){
            System.out.println("Cannot Parse Claimset");
        }

        // repo.clearCart(userId);


    }

}
