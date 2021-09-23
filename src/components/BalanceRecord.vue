<template>
    <b-row class="border-bottom">
      <b-col cols="6" class="mx-1" role="navigation" >
        <b-nav tabs small align="left">
          <b-nav-item @click="getItemsByCurrentMonth()" class="active">本月收支紀錄</b-nav-item>
          <b-nav-item @click="getItemsByLastMonth()">上月收支紀錄</b-nav-item>
        </b-nav>
        <!-- 設置sortable, 若同時設置busy,則sortable失效  -->
        <b-table sticky-header :items="items" :fields="fields" striped hover bordered small>   
        </b-table>
      </b-col>
      <b-col cols="4">
        <PieChart :chartData="chartdata" :options="options" v-if="loaded"/>
      </b-col>
    </b-row>
</template>

<script>
import axios from 'axios'
import PieChart from './PieChart.vue'
export default {
  name:'BalanceRecord',
  components:{PieChart},
  data(){
    return {
      fields:[
          {key:'date',label:'日期',sortable: true},
          {key:'amount',label:'金額',sortable: true,tdClass: 'setAmountTdClass'},
          {key:'category',label:'收支',sortable: true},
          {key:'type',label:'類型',sortable: true},
        ],
      items:[],
      loaded:false,
      chartdata: {
          labels: [],
          datasets: [
            {
              label: 'Weight track',
              backgroundColor: [ '#FFE153', '#C2FF68', '#2828FF', '#FFCBB3','#46A3FF', ' #FF95CA' ],
              data: [],
              borderColor: "rgba(1, 116, 188, 0.50)",
              pointBackgroundColor: "rgba(171, 71, 188, 1)"
            }
          ]
        },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        title:{
          display: true,
          text: '月支出圖表',
          position:'top',
          fontColor:'purple'
        },
        legend: {
          display: true,
          position: 'top',
        }, 
      }, 
    }
  },
  computed:{
    
  },
  watch:{
      chartdata () {
        this.$data._chart.update()
      }
    },
  methods:{
    // 處理收支紀錄表內,依金額,動態顯示顏色 
    setAmountTdClass(value){
      if(value > 0){
        return 'text-green'
      }else{
        return 'text-red'
      }
    },
    // 獲取當月收支資料
    getItemsByCurrentMonth(){
      axios.get('api/balance/getBalanceSheet')
        .then(resp =>{
          if('SUCCESS' === resp.data.result){
              this.items = resp.data.data
              this.getChartData()
          }else{
            alert(resp.data.message)
          }
        })
        .catch(error =>{
          alert(error.message)
        })  
    },
    // 獲取上月收支紀錄
    getItemsByLastMonth(){
      axios.get('api/balance/getBalanceSheet/lastMonth')
        .then(resp =>{
          if('SUCCESS' === resp.data.result){
            this.items = resp.data.data
            this.getChartByLastMonth()
          }else{
            alert(resp.data.message)
          }
        })
        .catch(error =>{
          alert(error.message)
        }) 
    },
    // 獲取當月支出圖表資料
    getChartData(){
      this.loaded = false
      axios.get('api/balance/getBalanceSheet/chart')
        .then(resp => {
          let respData = resp.data.data
          let labelsArr = []
          let dataArr = []
          respData.forEach(element => {
            labelsArr.unshift(element.type)
            dataArr.unshift(element.totalAmount)
          })
          this.chartdata.labels = labelsArr
          this.chartdata.datasets[0].data = dataArr
          this.loaded = true
        })
        .catch(error =>{
          alert(error.message)
        })
    },
    // 獲取上月支出圖表資料
    getChartByLastMonth(){
      this.loaded = false
      axios.get('api/balance/getBalanceSheet/lastMonth/chart')
        .then(resp => {
          let respData = resp.data.data
          let labelsArr = []
          let dataArr = []
          respData.forEach(element => {
            labelsArr.unshift(element.type)
            dataArr.unshift(element.totalAmount)
          })
          this.chartdata.labels = labelsArr
          this.chartdata.datasets[0].data = dataArr
          this.loaded = true
        })
        .catch(error =>{
          alert(error.message)
        })
    }

  },
  mounted(){
      this.getItemsByCurrentMonth()
      this.getChartData()
  }    
}
</script>

<style>
  /* 處理收支紀錄表內,依金額,動態顯示顏色 */
  .text-red {
    color: red;
  }
  .text-green {
    color: green;
  }
</style>