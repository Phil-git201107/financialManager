<template>
  <b-modal ref="editStockModal" title="編輯使用者資料" v-model="modalShow" @ok="editStockhandleOk" @cancel="resetModal" @close="resetModal" no-close-on-backdrop>
    <b-form-group label="代號:" label-for="stockNo" label-cols-sm="2" label-align-sm="right">
      <b-form-input id="stockNo" v-model="stock.stockNo" readonly ></b-form-input>
    </b-form-group>
    <b-form-group label="名稱:" label-for="name" label-cols-sm="2" label-align-sm="right">
      <b-form-input id="name" v-model="stock.stockName" readonly></b-form-input>
    </b-form-group>
    <b-form-group label="價格:" label-for="price" label-cols-sm="2" label-align-sm="right">
      <b-form-input id="price" :price="stock.price"></b-form-input>
    </b-form-group>
    <b-form-group label="數量:" label-for="amount" label-cols-sm="2" label-align-sm="right">
      <b-form-input id="amount" v-model="stock.amount"></b-form-input>
    </b-form-group>
  </b-modal>
</template>

<script>
  import {mapState} from 'vuex'
  export default {
    name:'EditStockModal',
    computed:{
      ...mapState('modal',['modalShow','stock']),
      modalShow:{
        get(){
          return this.$store.state.modal.modalShow
        },
        set(val){
          this.$store.commit('modal/UpdateModalShow',val)
        }
      }
    },
    methods:{
      editStockhandleOk(bvModalEvt){
        bvModalEvt.preventDefault()
        this.editStockOkSubmit()
      },
      editStockOkSubmit(){
        this.$store.dispatch('modal/editStock',this.stock)
        this.$store.state.modal.modalShow = false
      },
      resetModal(bvModalEvt){
        bvModalEvt.preventDefault()
        this.$store.state.modal.stock = {}
        this.$store.state.modal.modalShow = false
      }
    }
    
  }
</script>

<style>

</style>