<template>
  <b-container fluid="md" class="bv-example-row">

    
  </b-container>
</template>

<script>
export default {
  methods:{
    getStockRecord(){
        this.$store.dispatch('stockFile/getStockRecord')
    }    
  },
  mounted(){
    this.getStockRecord()
  }

}
</script>

<style>

</style>