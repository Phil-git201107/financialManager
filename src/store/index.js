import Vue from 'vue'
import Vuex from 'vuex'
import stockFile from './stockFile'
import modal from './modal'

Vue.use(Vuex)

export default new Vuex.Store({
  modules:{
    stockFile,
    modal,
  }
})