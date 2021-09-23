import axios from 'axios'

export default {
  namespaced: true,
  state:{
    modalShow: false,
    stock:{},
  },
  actions:{
    editStock(miniStore,data){
      axios.post('api/stock/update/oneStockFile',data)
        .then(resp =>{
          if('SUCCESS' === resp.data.result){
            // 跨組件引用stockFile裡的mutations
            console.log(resp.data.result)
            miniStore.dispatch('stockFile/getStockRecord', data,{ root: true })
            miniStore.commit('EditStock')
          }else{
            console.log(resp.data.result)
            alert(resp.data.message)
          }
        })
        .catch(error =>{
          alert(error.message)
        })
    }
    
  },
  mutations:{
    GetStock(state,data){
      state.stock = data
    },
    EditStock(state){
      state.modalShow = false
      state.stock = {}
    },
    UpdateModalShow(state,data){
      state.modalShow = data
    }
  },
  getters:{
    
  }
}