<template>
  <b-container fluid="sm" class="bv-example-row">
    <b-card no-body>
      <b-tabs v-model="tabIndex" card>
        <b-tab title="日 量價圖" >
          <ChartForStock :chartData="chartData" :options="options" v-if="dayLoaded"/>
        </b-tab>
        <b-tab title="月 量價圖" >
          <ChartForMonth :chartData="monthChartData" :options="options" v-if="monthLoaded"/>
        </b-tab>
      </b-tabs>
    </b-card>
    <!-- <ChartForStock :chartData="chartData" :options="options" v-if="dayLoaded"/> -->
    <b-row class="mt-2 class= justify-content-md-center">
      <b-col cols="12" class="mx-1">
        <b-table class="mt-1" sticky-header :items="stockRecord" :fields="fields" striped hover bordered small>   
          <template v-slot:cell(actions)="row">
            <b-button variant="outline-primary" size="sm" @click="getChartData(row)" class="mr-1">看圖</b-button>
          </template>  
        </b-table>
      </b-col>
    </b-row>
  </b-container>
</template>

<script>
  import axios from 'axios'
  import {mapState} from 'vuex'
  import ChartForStock from '@/components/ChartForStock'
  import ChartForMonth from '@/components/ChartForMonth'
  export default {
    name:'Performance',
    components:{ChartForStock,ChartForMonth},
    data(){
      return {
        fields:[
          { key: 'date', label: '日期' },
          { key: 'stockName', label: '名稱' },
          { key: 'price', label: '成本均價' },
          { key: 'amount', label: '庫存數(股)' },
          { key: 'currentPrice', label: '市價' },
          { key: 'profitOrLoss', label: '損益',sortable: true,tdClass: 'setprofitOrLossTdClass'},
          { key: 'actions', label: '操作',tdClass: 'align-middle' }
        ],
        dateArr:[],
        priceArr:[],
        amountArr:[],
        dayLoaded:false,
        monthLoaded:false,
        tabIndex: 0,
        chartData: {
          labels: [],
          datasets: [
            {
              type:'line',
              label: '收盤價',
              yAxisID:'A',
              backgroundColor: this.gradient,
              data: [],
              borderColor: 'green',
              pointBackgroundColor: "rgba(171, 71, 188, 1)"
            },
            {
              type:'bar',
              label: '均量',
              yAxisID:'B',
              backgroundColor: '#f87979',
              data: [],
              pointBorderColor: '#249EBF',
              pointBackgroundColor: 'white'
            },
          ]
        },
        monthChartData: {
          labels: [],
          datasets: [
            {
              type:'line',
              label: '收盤價',
              yAxisID:'A',
              backgroundColor: this.gradient,
              data: [],
              borderColor: 'green',
              pointBackgroundColor: "rgba(171, 71, 188, 1)"
            },
            {
              type:'line',
              label: '均量',
              yAxisID:'B',
              backgroundColor: '#f87979',
              data: [],
              borderColor: '#249EBF',
              pointBackgroundColor: 'white'
            },
          ]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          title:{
            display: true,
            text: '',
            position:'top',
            fontColor:'purple'
          },
          scales:{
            yAxes:[
              {
                id:'A',
                type:'linear',
                position:'left',
              },
              {
                id:'B',
                type:'linear',
                position:'right',
              }
            ],
            xAxes:[
              {
                gridLines:{display:false}
              }
            ]
          } 
        },
      }
    },
    watch:{
      chartData () {
        this.$data._chart.update()
      },
      monthChartData(){
        this.$data._chart.update()
      }
    },
    computed:{
      ...mapState('stockFile',['stockRecord'])
    },
    methods:{
      // 處理收支紀錄表內,依金額,動態顯示顏色 
      setprofitOrLossTdClass(value){
        if(value.indexOf("-") != -1){
          return 'text-red'
        }else{
          return 'text-green'
        }
      },
      getChartData(row){
        this.monthLoaded = false
        this.dayLoaded = false
        let stockNo = row.item.stockNo
        // 獲取monthStockInfo
        axios.get('api/stock/get/monthStockInfo',{params: {'stockNo': stockNo}})
          .then(resp =>{
            if('SUCCESS' === resp.data.result){
              let dayStockArr = resp.data.data
              // 接收日期的array
              let dateArr = []
              // 接收收盤價的array
              let priceArr = []
              // 接收成交量的array
              let amountArr = []
              dayStockArr.forEach(element => {
                dateArr.push(element.date)
                priceArr.push(element.price)
                amountArr.push(element.amount)
              })
              this.monthChartData.labels = dateArr
              this.monthChartData.datasets[0].data = priceArr
              this.monthChartData.datasets[1].data = amountArr
              this.options.title.text = stockNo
              this.monthLoaded = true
            }else{
              alert(resp.data.result)
            }
          })
          .catch(error =>{
            alert(error.message)
          })
        // 獲取dayStockInfo
        axios.get('api/stock/get/dayStockInfo',{params: {'stockNo': stockNo}})
          .then(resp =>{
            if('SUCCESS' === resp.data.result){
              let dayStockArr = resp.data.data
              // 接收日期的array
              let dateArr = []
              // 接收收盤價的array
              let priceArr = []
              // 接收成交量的array
              let amountArr = []
              dayStockArr.forEach(element => {
                dateArr.push(element.date)
                priceArr.push(element.price)
                amountArr.push(element.amount)
              })
              this.chartData.labels = dateArr
              this.chartData.datasets[0].data = priceArr
              this.chartData.datasets[1].data = amountArr
              this.dayLoaded = true
            }else{
              alert(resp.data.result)
            }
          })
          .catch(error =>{
            alert(error.message)
          })  
      }
    }
  }
</script>

<style>

</style>