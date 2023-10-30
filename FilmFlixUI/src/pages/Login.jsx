import React from "react";
import {useUser} from "hook/User";
import styled from "styled-components";
import {useForm} from "react-hook-form";
import {login} from "backend/idm";
import TextField from '@mui/material/TextField';
import Alert from '@mui/material/Alert';
import { useNavigate } from 'react-router-dom';



const StyledDiv = styled.div`
display: flex;
  flex-direction: column;
  width: 500px;
  margin: auto; /* Horizontally centers the div */
  align-items: center; /* Horizontally centers the content */
  margin-top:-250px;
`




const Background = styled.div`
    background-image: url(${require('../img/bg.png')});
    background-repeat: no-repeat;
    background-size: cover;
    padding-bottom:117px;
    overflow: hidden; /* Hide scroll bars */
`

const StyledH1 = styled.h1`
`

const StyledInput = styled.input`
`

const StyledButton = styled.button`
`
/**
 * useUser():
 * <br>
 * This is a hook we will use to keep track of our accessToken and
 * refreshToken given to use when the user calls "login".
 * <br>
 * For now, it is not being used, but we recommend setting the two tokens
 * here to the tokens you get when the user completes the login call (once
 * you are in the .then() function after calling login)
 * <br>
 * These have logic inside them to make sure the accessToken and
 * refreshToken are saved into the local storage of the web browser
 * allowing you to keep values alive even when the user leaves the website
 * <br>
 * <br>
 * useForm()
 * <br>
 * This is a library that helps us with gathering input values from our
 * users.
 * <br>
 * Whenever we make a html component that takes a value (<input>, <select>,
 * ect) we call this function in this way:
 * <pre>
 *     {...register("email")}
 * </pre>
 * Notice that we have "{}" with a function call that has "..." before it.
 * This is just a way to take all the stuff that is returned by register
 * and <i>distribute</i> it as attributes for our components. Do not worry
 * too much about the specifics of it, if you would like you can read up
 * more about it on "react-hook-form"'s documentation:
 * <br>
 * <a href="https://react-hook-form.com/">React Hook Form</a>.
 * <br>
 * Their documentation is very detailed and goes into all of these functions
 * with great examples. But to keep things simple: Whenever we have a html with
 * input we will use that function with the name associated with that input,
 * and when we want to get the value in that input we call:
 * <pre>
 * getValue("email")
 * </pre>
 * <br>
 * To Execute some function when the user asks we use:
 * <pre>
 *     handleSubmit(ourFunctionToExecute)
 * </pre>
 * This wraps our function and does some "pre-checks" before (This is useful if
 * you want to do some input validation, more of that in their documentation)
 */
const Login = () => {

    const {
        accessToken, setAccessToken,
        refreshToken, setRefreshToken
    } = useUser();

    const {register, getValues, handleSubmit} = useForm();
    const [showAlert, setShowAlert] = React.useState(false);
    const navigate = useNavigate();

    const submitLogin = () => {
        const email = getValues("email");
        const password = getValues("password");

        const payLoad = {
            email: email,
            password: password
        }

        login(payLoad)
            .then(response => {
                setAccessToken(response.data.accessToken)
                navigate('/search');
            })
        .catch(error=>{
            if(error.response && error.response.status === 401){
                setShowAlert(true);
            }
        });
        
    }

    return (
        <div className="hidden">
        <Background>
        <StyledDiv>
            <h1 style={{marginTop:'400px',fontFamily:'Righteous',color:'white'}}>Login</h1>
            <TextField {...register("email")} type={"email"} id="filled-basic" label="Email" variant="filled" style={{width:'400px',marginTop:'30px',marginBottom:'30px'}}/>
            <TextField {...register("password")} type={"password"} id="filled-basic" label="Password" variant="filled" style={{width:'400px',marginTop:'30px',marginBottom:'30px'}}/>

            {/* <input {...register("email")} type={"email"} style={{width:'400px',marginTop:'30px',marginBottom:'30px'}}/>
            <input {...register("password")} type={"password"}/> */}
            <button onClick={handleSubmit(submitLogin)}>Login</button>
        
        </StyledDiv>

        {showAlert && (
          <Alert sx ={{position:'absolute',marginLeft:2,marginTop:2,width:500,marginBottom:0}} severity="error" onClose={() => setShowAlert(false)}>
            User not found. Please check your credentials.
          </Alert>
        )}
        </Background>

        </div>
    );
}

export default Login;
