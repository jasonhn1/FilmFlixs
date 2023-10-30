import Config from "backend/config.json";
import Axios from "axios";
import insertCartParams from "../models/insertCartParams";
import completeParams from "../models/completeParams";

export async function insertItem(accessToken,movie_id,quantity) {
    
    
    const options = {
        method: "POST", // Method type
        baseURL:Config.billingBaseUrl, // Base part of URL
        url: "/cart/insert", // Path part of URL,
        data: new insertCartParams(movie_id,quantity), // since we are sending a post request we send the params through data
        headers: {
            Authorization: "Bearer " + accessToken
        }
    }

    return Axios.request(options);
}

export async function getCart(accessToken) {
    
    
    const options = {
        method: "GET", // Method type
        baseURL:Config.billingBaseUrl, // Base part of URL
        url: "/cart/retrieve", // Path part of URL,
        headers: {
            Authorization: "Bearer " + accessToken
        }
    }

    return Axios.request(options);
}

export async function deleteItem(accessToken,movieId) {
    
    const options = {
        method: "DELETE", // Method type
        baseURL:Config.billingBaseUrl, // Base part of URL
        url: "/cart/delete/"+movieId, // Path part of URL,
        headers: {
            Authorization: "Bearer " + accessToken
        }
    }

    return Axios.request(options);
}



export async function updateItem(accessToken,movie_id,quantity) {
    
    
    const options = {
        method: "POST", // Method type
        baseURL:Config.billingBaseUrl, // Base part of URL
        url: "/cart/update", // Path part of URL,
        data: new insertCartParams(movie_id,quantity), // since we are sending a post request we send the params through data
        headers: {
            Authorization: "Bearer " + accessToken
        }
    }

    return Axios.request(options);
}



export async function getPaymentIntent(accessToken) {
    
    
    const options = {
        method: "GET", // Method type
        baseURL:Config.billingBaseUrl, // Base part of URL
        url: "/order/payment", // Path part of URL,
        headers: {
            Authorization: "Bearer " + accessToken
        }
    }

    return Axios.request(options);
}




export async function completePayment(accessToken,paymentIntentId) {
    
    
    const options = {
        method: "POST", // Method type
        baseURL:Config.billingBaseUrl, // Base part of URL
        url: "/order/complete", // Path part of URL,
        data: new completeParams(paymentIntentId),
        headers: {
            Authorization: "Bearer " + accessToken
        }
    }

    return Axios.request(options);
}


export async function getSales(accessToken) {
    
    
    const options = {
        method: "GET", // Method type
        baseURL:Config.billingBaseUrl, // Base part of URL
        url: "/order/list", // Path part of URL,
        headers: {
            Authorization: "Bearer " + accessToken
        }
    }

    return Axios.request(options);
}