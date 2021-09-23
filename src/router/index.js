import VueRouter from 'vue-router'

import MainView from '../MainView.vue'
import BalanceSheet from '../pages/BalanceSheet.vue'
import StockInvest from '../pages/StockInvest.vue'
import StockFile from '../pages/StockFile.vue'
import Performance from '../pages/Performance.vue'

export default new VueRouter({
  routes:[
    {
      path:'/',
      name:'MainView',
      component:MainView,
      children:[
        {path:'balancesheet',name:'BalanceSheet',component:BalanceSheet},
        {path:'stockinvest',name:'StockInvest',component:StockInvest},
        {path:'stockfile',name:'StockFile',component:StockFile},
        {path:'performance',name:'Performance',component:Performance}
      ]
    },
    
  ]
})