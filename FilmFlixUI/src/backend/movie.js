import Config from "backend/config.json";
import Axios from "axios";




export async function movieSearch(accessToken,searchQueryParams) {
    
    const options = {
        method: "GET", // Method type
        baseURL:Config.movieBaseUrl, // Base part of URL
        url: "/movie/search", // Path part of URL,
        params: searchQueryParams,
        headers: {
            Authorization: "Bearer " + accessToken
        }
    }
    return Axios.request(options);
}


export async function detailMovieSearch(accessToken,id) {
    var movie = ("/movie/"+id);
    const options = {
        method: "GET", // Method type
        baseURL:Config.movieBaseUrl, // Base part of URL
        url: movie, // Path part of URL,
        headers: {
            Authorization: "Bearer " + accessToken
        }
    }

    return Axios.request(options);
}


