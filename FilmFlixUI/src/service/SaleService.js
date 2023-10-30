import {getSales} from "backend/billing"

class SaleService {

    constructor() {
        this.data = {};
    }

    getData() {
        return this.data;
    }

    setData(accessToken) {
        getSales(accessToken)
        .then( (response)=>{ 
            console.log("YEAT");
            if (response.data.result.code === 3081){
                this.data = {};
            }else{
                this.data = response.data.sales
            }
            
         //   console.log(response)
    })

}

}

export default new SaleService;