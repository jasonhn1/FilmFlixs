import React,{useRef} from "react";
import {Route, Routes} from "react-router-dom";

import Login from "pages/Login";
import Home from "pages/Home";
import Register from "pages/Register";
import styled from "styled-components";
import MovieDetail from "pages/MovieDetail";
import Search from "pages/Search";
import Cart from "pages/Cart";
import Order from "pages/Order";
import {useUser} from "hook/User";
import {getCart} from "backend/billing"
import CheckOut from "pages/CheckOut";
import {getSales} from "backend/billing"

const StyledDiv = styled.div`

  width: 100vw;
  height: 100vh;

`

/**
 * This is the Component that will switch out what Component is being shown
 * depending on the "url" of the page
 * <br>
 * You'll notice that we have a <Routes> Component and inside it, we have
 * multiple <Route> components. Each <Route> maps a specific "url" to show a
 * specific Component.
 * <br>
 * Whenever you add a Route here make sure to add a corresponding NavLink in
 * the NavBar Component.
 * <br>
 * You can essentially think of this as a switch statement:
 * @example
 * switch (url) {
 *     case "/login":
 *         return <Login/>;
 *     case "/":
 *         return <Home/>;
 * }
 *
 */
const Content = () => {
    const [ cart, setCart ] = React.useState([]);
    const [sales,setSales] = React.useState();

    const [bool,setBool] = React.useState(true);
    const {
        accessToken, setAccessToken,
        refreshToken, setRefreshToken
    } = useUser();
   

    React.useEffect(() => {

    getSales(accessToken)
        .then( (response)=>{ 
            if(response.data.result.code == 3081){
                setSales(false)
            }else{
                setSales(response.data);
            }
    })
    .catch(e => {
        console.log(e);
    });

    
    getCart(accessToken)
    .then(response =>{ 
       // console.log(response)
        if(response.data.result.code == 3004){
            setCart(false)
        }else{
            setCart(response.data.items)
        }
         })
    .catch(e => {
            console.log(e);
        });
   }, []);

   
   const retrieveOrders = () => {
//     getSales(accessToken)
//     .then(response =>{ setSales(response.data)
// })
console.log("Nut");
}

   const retrieveCart = () => {
        getCart(accessToken)
        .then(response =>{ setCart(response.data.items)
    }).catch(e => {
        console.log(e);
    });

};
    return (
        <StyledDiv>
            <Routes>
                <Route path="/login" element={<Login/>}/>
                <Route path="/" element={<Home/>}/>
                <Route path="/register" element = {<Register/>} />
                <Route path="/search" element = {<Search/>} />
                <Route path="/movie/:id" element={<MovieDetail retrieveCart = {retrieveCart}/> }/>
                <Route path="/cart" element={<Cart movies={cart}/>}/>
                <Route path="/checkout" element={<CheckOut/>}/>
                <Route path="/order" element={<Order sales={sales}/>}/>
            </Routes>
        </StyledDiv>
    );
}

export default Content;
