<template>
  <b-row class="border-bottom">
    <b-col cols="12" class="mx-1">
      <span style="font-size: 22px;color: purple;font-weight:bold;">庫存股票 &nbsp;&nbsp;&nbsp;
        <span style="font-size: 16px;margin-left: 20rem;" >
          <span>
            (投入金額={{totalCostStr}},市值={{marketValueStr}},合計損益金額=
          </span>
          <span :style="{'color': colorStr ? 'green' : 'red'}">{{totalPorLStr}}</span>)
      </span>  
      </span>  
      <b-table class="mt-1" sticky-header :items="stockRecord" :fields="fields" striped hover bordered small>   
        <template v-slot:cell(actions)="row">
          <b-button variant="outline-primary" size="sm" @click="updateStockFile(row)" class="mr-1">修改</b-button>
          <b-button variant="outline-danger" size="sm" @click="deleteStockFile(row)">刪除</b-button>
        </template> 
      </b-table>
    </b-col>
    <!-- 修改個股資料modal -->
    <EditStockModal />
  </b-row>
</template>

<script>
  import {mapState,mapGetters} from 'vuex'
  import EditStockModal from './EditStockModal.vue'
  export default {
    name:'StockRecord',
    components:{EditStockModal},
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
        ] 
      }
    },
    computed:{
      ...mapState('stockFile',['isShow','stockRecord']),
      ...mapGetters('stockFile',['totalCostStr','marketValueStr','totalPorLStr'])
    },
    methods:{
      updateStockFile(row){
        this.$store.commit('modal/GetStock',row.item)
        this.$store.state.modal.modalShow = true
      },
      deleteStockFile(row){
        this.$bvModal.msgBoxConfirm('確認刪除 『' + row.item.stockName + '』 ?', {
          title: 'Please Confirm',
          okVariant: 'success',
          okTitle: 'YES',
          cancelTitle: 'NO',
          centered: true,
          noCloseOnBackdrop: true
        }).then(value => {
            if (value === true) {
              let stockNo = {id: row.item.stockNo}
              this.$store.dispatch('stockFile/delStockOne',stockNo)
            }
          })
      },
      // 處理收支紀錄表內,依金額,動態顯示顏色 
      setprofitOrLossTdClass(value){
        if(value.indexOf("-") != -1){
          return 'text-red'
        }else{
          return 'text-green'
        }
      },
      colorStr(){
        return (this.totalPorLStr.indexOf('-') !== -1)
      }
      
    },  
    mounted(){
    }
  }  
</script>

<style scoped>

</style>