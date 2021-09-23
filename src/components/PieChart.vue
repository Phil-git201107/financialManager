<script>
  import { Pie, mixins } from 'vue-chartjs'
  const {reactiveProp} = mixins
  export default {
    // extends: Pie,
    mixins:[Pie,reactiveProp],
    props:['chartData','options'],
    data(){
      return {
      
      }
    },
    mounted(){
      this.renderChart(this.chartData, this.options)
      
    }    
  }
</script>

<style>

</style>