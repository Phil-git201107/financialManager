<template>
  <b-container fluid="sm" class="bv-example-row">
    <StockRecord />
    <b-row class="mt-2 class= justify-content-md-center">
      <b-card bg-variant="light" class="w-50">
          <b-form-group
            label-cols-lg="3"
            label="股票建檔"
            label-size="lg"
            label-class="font-weight-bold pt-0"
            class="mb-0"
          >
            <b-form-group
              label="日期:"
              label-for="date"
              label-cols-sm="4"
              label-align-sm="right"
            >
              <b-form-datepicker id="date" v-model="stockFile.date" class="mb-2" :max="max" selected-variant="warning"></b-form-datepicker>
            </b-form-group>
            <b-form-group
              label="股票代碼:"
              label-for="stockNo"
              label-cols-sm="4"
              label-align-sm="right"
            >
              <b-form-input id="stockNo" v-model="stockFile.stockNo" ></b-form-input>
                <search-icon size="1.5x" class="custom-class"></search-icon>
                <a href='https://tw.stock.yahoo.com/h/kimosel.php'  target="_blank">股票代號查詢</a>
            </b-form-group>
            <b-form-group
              label="金額:"
              label-for="price"
              label-cols-sm="4"
              label-align-sm="right"
            >
            <!-- 本輸入框,只能輸入最多含兩位小數的正數 -->
              <b-form-input id="price" v-model="stockFile.price"
                  onkeyup="if(
                            ! /^[0-9]+(.[0-9]{0,2})?$/.test(this.value)){alert('只能輸入數字，小數點後只能保留兩位');this.value='';}">
                </b-form-input>
            </b-form-group>
            <b-form-group
              label="數量:"
              label-for="amount"
              label-cols-sm="4"
              label-align-sm="right"
              >
              <!-- 本輸入框,能輸入正整數 -->
              <b-form-input id="amount" v-model="stockFile.amount"
                    onkeyup="value=value.replace(/^(0+)|[^\d]+/g,'')" v-b-tooltip.focus.bottomright.v-danger title="請輸入股數,不要輸入張數!!!">
              </b-form-input>
            </b-form-group>
          </b-form-group>
          <b-form-group label-cols-sm="6" label-align-sm="right">
            <b-overlay
              :show="busy"
              rounded
              opacity="0.6"
              spinner-small
              spinner-variant="primary"
              class="d-inline-block"
            >
             <b-button ref="button" variant="outline-warning" @click="stockFileSubmit()">提交</b-button>
            </b-overlay> 
          </b-form-group>
                
      </b-card>  
    </b-row>
  </b-container>      
</template>

<script>
import axios from 'axios'
import {SearchIcon} from 'vue-feather-icons'
import StockRecord from '../components/StockRecord.vue'
export default {
  name:'StockFile',
  components:{SearchIcon,StockRecord},
  data(){
    return {
      stockFile:{},
      max: new Date(),
      busy:false
    }
  },
  methods:{
    stockFileSubmit(){
      if(!(this.stockFile.date) || !(this.stockFile.stockNo) || !(this.stockFile.price) || !(this.stockFile.amount)){
          return alert('所有欄位都須填入資料!')
      }
      this.stockFile.date = this.stockFile.date.replace(/-/g,'');
      this.busy = true
      let stockNoForShow = this.stockFile.stockNo
      axios.put('api/stock/save/stockFile',this.stockFile)
        .then(resp =>{
          if('SUCCESS' === resp.data.result){
            alert("已為 "+stockNoForShow+" 建立資料")
            this.$store.dispatch('stockFile/getStockRecord')
          }else{
            alert(resp.data.message)
          }
          this.busy = false
        })
        .catch(error =>{
          this.busy = false
          // console.log(error.code)
          console.log(error.message)
          // console.log(error.stack)
        })
      this.stockFile = {}  
    }
  },
  mounted(){
      
  }
}
</script>

<style>

</style>