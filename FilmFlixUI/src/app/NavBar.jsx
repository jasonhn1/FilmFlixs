import React from "react";
import {NavLink} from "react-router-dom";
import styled from "styled-components";
import {useUser} from "hook/User";
import {getCart} from "backend/billing"
import SaleService from "service/SaleService";
import logo from "../img/logo.png"

const Nav = styled.div`
  position: fixed;
  left: 0;
  right: 0;
  top: 0;
  display: flex;
  justify-content: end;
  height: 80px;
  padding: 0.5rem calc((100vw - 1000px)/2);
  z-index:101;
  background-color: #031f36;
`;

const NavMenu = styled.div`
  display: flex;
  margin-right:40px;
  @media screen and (max-width:768){
      display:none;
  }
`;

const StyledNavLink = styled(NavLink)`

  font-size: 25px;
  color: #A890FE;
  text-decoration: none;
  font-family: 'Pacifico';

`;

const NavLogo = styled(NavLink)`

padding: 10px;
margin-left:30px;
font-size: 30px;
position:absolute;
left:0;
top:.5;
font-family:Righteous;
letter-spacing: .2rem;
background: #EA8D8D;
background: linear-gradient(to left, #EA8D8D 24%, #A890FE 100%);
-webkit-background-clip: text;
-webkit-text-fill-color: transparent;
`;


const Logo = styled.img`
width:30px;
height:30px;
position:absolute;
margin-left:10px;
margin-top:2px;
`;

const NavButton = styled.div`
display: flex;
justify-content: end;

`;

const RegisterButton = styled.div`
font-family: 'Pacifico', cursive;
text-decoration:none;

padding-left:10px;
padding-bottom:15px;
padding-top:0px;
border-radius: 20px;
border: 3px solid white;
width: 90px;
height: 30px;
`


/**
 * To be able to navigate around the website we have these NavLink's (Notice
 * that they are "styled" NavLink's that are now named StyledNavLink)
 * <br>
 * Whenever you add a NavLink here make sure to add a corresponding Route in
 * the Content Component
 * <br>
 * You can add as many Link as you would like here to allow for better navigation
 * <br>
 * Below we have two Links:
 * <li>Home - A link that will change the url of the page to "/"
 * <li>Login - A link that will change the url of the page to "/login"
 */
const NavBar = () => {
    const {
        accessToken, setAccessToken,
        refreshToken, setRefreshToken
    } = useUser();

    const getOrder = () =>{

        SaleService.setData(accessToken);
        console.log(SaleService.getData());
    }

    return (
        <Nav>
            <NavLogo to="/">
                FilmFlix
            <Logo style = {{marginTop:7}} src = {logo}/>
            </NavLogo>
            

            <NavMenu>
            {/* <StyledNavLink style={{color:'#ea8d8d'}} to="/login">
                login
            </StyledNavLink> */}

            {accessToken && 
            <StyledNavLink style ={{marginTop:12,marginRight:30}} to="/search">
                Search
            </StyledNavLink>}
            {accessToken && 
            <StyledNavLink style ={{marginTop:12,marginRight:30}} to="/cart">
                Cart
            </StyledNavLink>
                }

{accessToken && 
            <StyledNavLink style ={{marginTop:12}} onClick = {getOrder} to="/order">
                Order
            </StyledNavLink>
}
            </NavMenu>

            <NavButton>
            {/* <StyledNavLink style={{color:'#b090f2'}} to="/register">
                register
            </StyledNavLink> */}
            </NavButton>
        </Nav>
    );
}

export default NavBar;
