import React, { useState, useEffect } from "react";
import { loadStripe } from "@stripe/stripe-js";
import { Elements } from "@stripe/react-stripe-js";

import CheckoutForm from "./CheckoutForm";
import "./Checkout.css";
import {useUser} from "hook/User";
import {getPaymentIntent} from "backend/billing"

// Make sure to call loadStripe outside of a component’s render to avoid
// recreating the Stripe object on every render.
// This is your test publishable API key.
const stripePromise =
 loadStripe("pk_test_51Nf8rEFUF4CvQtKZisSZFe8f72CyiabdSk5FaLfciMLYpBvGFr1uAm2LTwCN4OeleYur7hHROvHR9H4VXioxxXJA00JYVT29TW");

export default function CheckOut() {
  const [clientSecret, setClientSecret] = useState("");
  const {
    accessToken, setAccessToken,
    refreshToken, setRefreshToken
} = useUser();

    const [paymentIntentId,setPaymentIntentId] = useState();

  useEffect(() => {
    // Create PaymentIntent as soon as the page loads

    getPaymentIntent(accessToken).then(res=>{
        setClientSecret(res.data.clientSecret);
        setPaymentIntentId(res.data.paymentIntentId)
        console.log(res)})

    // fetch("/create-payment-intent", {
    //   method: "POST",
    //   headers: { "Content-Type": "application/json" },
    //   body: JSON.stringify({ items: [{ id: "xl-tshirt" }] }),

    // })
    //   .then((res) => res.json())
    //   .then((data) => setClientSecret(data.clientSecret));
  }, []);

  const appearance = {
    theme: 'stripe',
  };
  const options = {
    clientSecret,
    appearance,
  };

  return (
    <div className = "hidden" style ={{marginLeft:300,marginTop:100}}>
        <p>Check Out Page</p>
      {clientSecret && (
        <Elements options={options} stripe={stripePromise}>
          <CheckoutForm paymentIntentId={paymentIntentId}/>
        </Elements>
      )}
    </div>
  );
}