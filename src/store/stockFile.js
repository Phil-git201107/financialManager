import axios from 'axios'

export default {
  namespaced: true,
  state:{
    isShow: false,
    stockRecord:[],
  },
  actions:{
    getStockRecord(miniStore){
      axios.get('api/stock/get/allStockFile')
        .then(resp => {
          if('SUCCESS' === resp.data.result){
            miniStore.commit('GetStockRecord',resp.data.data)
          }
        })
        .catch(error =>{
          alert(error.message)
        })
    },
    delStockOne(miniStore,stockNo){
      axios.delete('api/stock/delete/oneStock',{params:stockNo})
        .then(resp =>{
          if('SUCCESS' === resp.data.result){
            miniStore.commit('DelStockOne',resp.data.data)
          }
        })
        .catch(error =>{
          alert(error.message)
        })
    }
  },
  mutations:{
    GetStockRecord(state,data){
      state.stockRecord = data
      state.isShow = true
    },
    DelStockOne(state,data){
      state.stockRecord = data
    }
  },
  getters:{
    totalCostStr(state){
      let arr = state.stockRecord
      let totalCost = 0
      arr.forEach(element =>{
        totalCost += element.cost
      })
      // 為了加千分位,需要轉成字串
      let totalCostStr = totalCost.toString()
      totalCostStr = totalCostStr.replace(/\B(?<!\.\d*)(?=(\d{3})+(?!\d))/g, ",")
      return totalCostStr
    },
    marketValueStr(state){
      let arr = state.stockRecord
      let marketValue = 0
      arr.forEach(element =>{
        marketValue += element.currentPrice*element.amount
      })
      marketValue = marketValue.toFixed(0)
      // 為了加千分位,需要轉成字串
      let marketValueStr = marketValue.toString()
      marketValueStr = marketValueStr.replace(/\B(?<!\.\d*)(?=(\d{3})+(?!\d))/g, ",")
      return marketValueStr
    },
    totalPorLStr(state){
      let arr = state.stockRecord
      let totalPorL = 0
      arr.forEach(element =>{
        totalPorL += (element.currentPrice*element.amount)-element.cost
      })
      totalPorL = totalPorL.toFixed(0)
      // 為了加千分位,需要轉成字串
      let totalPorLStr = totalPorL.toString()
      totalPorLStr = totalPorLStr.replace(/\B(?<!\.\d*)(?=(\d{3})+(?!\d))/g, ",")

      return totalPorLStr
    },
    chartLabel(state){
      let arr = state.stockRecord
      let chartLabel = []
      arr.forEach(element => {
        chartLabel.unshift(element.stockName)
      });
      return chartLabel
    },
    dataForChart(state){
      let arr = state.stockRecord
      let dataForChart = []
      arr.forEach(element =>{
        dataForChart.unshift(element.price*element.amount)
      })
      return dataForChart
    }
  }
}