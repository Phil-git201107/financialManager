<template>
    <b-container fluid="sm" class="bv-example-row">
      <BalanceRecord />
      <b-row class="mt-2 class= justify-content-md-center">
        <b-card bg-variant="light" class="w-50">
          <b-form-group
            label-cols-lg="3"
            label="收支登錄"
            label-size="lg"
            label-class="font-weight-bold pt-0"
            class="mb-0"
          >
            <b-form-group
              label="日期:"
              label-for="date"
              label-cols-sm="3"
              label-align-sm="right"
            >
              <b-form-datepicker id="date" v-model="balanceSheet.date" class="mb-2" :max="max" selected-variant="warning"></b-form-datepicker>
            </b-form-group>
            <b-form-group
              label="金額:"
              label-for="amount"
              label-cols-sm="3"
              label-align-sm="right"
            >
              <b-form-input id="amount" v-model="balanceSheet.amount"
              onkeyup="value=value.replace(/^(0+)|[^\d]+/g,'')">
              </b-form-input>
            </b-form-group>
            <b-form-group
              label="種類:"
              label-for="category"
              label-cols-sm="3"
              label-align-sm="right"
            >
              <b-form-select v-model="balanceSheet.category" :options="categoryOptions"></b-form-select>
            </b-form-group>
            <b-form-group
              label="類型:"
              label-for="type"
              label-cols-sm="3"
              label-align-sm="right"
            >
              <b-form-select v-model="balanceSheet.type" :options="typeOptions"></b-form-select>
            </b-form-group>
          </b-form-group>
          <b-form-group label-cols-sm="6" label-align-sm="right">
            <b-button variant="outline-warning" @click="balanceSheetSubmit">提交</b-button>
          </b-form-group>
          
        </b-card>
      </b-row>
    </b-container>
</template>

<script>
  import BalanceRecord from '@/components/BalanceRecord'
  import axios from 'axios'
  export default {
    name:'BalanceSheet',
    components:{BalanceRecord},
    data(){
      return {
        balanceSheet:{},
        categoryOptions:['收入','支出'],
        max: new Date()
      }
    },
    computed:{
      typeOptions: function(){
        return this.balanceSheet.category === '收入' ? ['薪資','其他']:['食','衣','住','行','育','樂']
      }
    },
    methods:{
      // 提交登錄的收支內容
      balanceSheetSubmit(){
        if(!(this.balanceSheet.date || this.balanceSheet.amount)){
          return alert('所有欄位都須填入資料!')
        }
        if(this.balanceSheet.category === undefined || this.balanceSheet.type === undefined){
          return alert('所有選項都必須選擇!')
        }
        this.balanceSheet.date = this.balanceSheet.date.replace(/-/g,'');
        console.log("this.balanceSheet.date",this.balanceSheet.date)
        axios.put('api/balance/balanceSheet/save',this.balanceSheet)
          .then(resp =>{
            if('SUCCESS' === resp.data.result){
              alert("提交成功,可以登錄下一筆了!")
            }else{
              alert(resp.data.message)
            }
          })
          .catch(error =>{
            alert(error.message)
          })  
        this.balanceSheet = {}
      },
    },
  }
</script>

<style>

</style>