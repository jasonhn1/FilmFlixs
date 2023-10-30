import React, { useState,useEffect } from "react";
import {useUser} from "hook/User";
import SaleService from "service/SaleService";
import { getSales } from "backend/billing";

const Order = (props) => {
    const {
        accessToken, setAccessToken,
        refreshToken, setRefreshToken
    } = useUser();

    // //SaleService.setData(accessToken);
    // React.useEffect(() => {
    //     SaleService.setData(accessToken);
    //   // console.log("We did it");
    //    },[]);
    
    //     var inital = 0;
    // getSales(accessToken).then(response => {
    //   //  console.log("NEVER SAY NEVER");
    //     if (response.data.result.code === 3081){
            
    //         inital= {};
    //     }else{
    //         inital = response.data.sales;
    //     }
    // })

   


    // // if the service is empty ||  SaleService.getData() === {}
    // // if(Object.keys(SaleService.getData()).length === 0){
    // //     console.log("pre")
    // //     console.log(SaleService.getData())
    // //     console.log("out")
    // //     inital= {};
    // // }else{
    // //     inital = SaleService.getData();
    // //     console.log("in")
    // //     console.log(inital);
    // //  //   console.log( !(Object.keys(inital).length === 0));
    // // }

    // const [sales,setSales] = useState(inital);

    // // //var sals = SaleService.getData();
    // // useEffect(() => {
    // //     setSales(SaleService.getData());
    // //  //   console.log(sales);
    // //    }, [SaleService.getData]);
  

    return (
        <>
        <div style={{marginTop:100}}>
            <h1 id="order">Order History</h1>
             { props.sales && props.sales.sales.map( element =>
            <div>
                <div>{element.saleId}</div>
                <div>{element.orderDate}</div>
                <div>${element.total.toFixed(2)}</div>
                {/* <div>${(element.total / 100).toFixed(2).toString().toLocaleString("en-US")}</div> */}
                <hr></hr>
             </div>)}  
         {/* { !(Object.keys(sales).length === 0) && sales.map( element =>
            <div>
                <div>{element.saleId}</div>
                <div>{element.orderDate}</div>
                <div>${(element.total / 100).toFixed(2).toString().toLocaleString("en-US")}</div>
                <hr></hr>
             </div>)}   */}
{/* 
{ sales.map( element =>
            <div>
                <div>{element.saleId}</div>
                <div>{element.orderDate}</div>
                <div>${(element.total / 100).toFixed(2).toString().toLocaleString("en-US")}</div>
                <hr></hr>
             </div>)}  
              */}

            
        </div>
        
        </>
    );
}

export default Order;
