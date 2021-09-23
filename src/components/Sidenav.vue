<template>
  <nav id="sidebarMenu" class="col-md-3 col-lg-2 d-md-block mt-5 bg-light sidebar collapse">
      <div class="sidebar-sticky pt-3">
        <ul class="nav flex-column">
          <li class="nav-item">
            <p>
              <home-icon size="1.5x" color="blue" class="custom-class"></home-icon>
              功能目錄 <span class="sr-only">(current)</span>
            </p>
          </li>
          <li class="nav-item">
            <router-link to="/balancesheet">
              <file-text-icon size="1.5x" color="blue" class="custom-class"></file-text-icon>
              收入與支出
            </router-link>
          </li>
          <li class="nav-item mt-2" >
            <router-link to="/stockinvest" v-b-toggle="'collapse-stock'">
              <award-icon size="1.5x" color="blue" class="custom-class"></award-icon>
              股票投資
            </router-link>
            <div class="ml-5" style="font-size: 18px;">
              <b-collapse id="collapse-stock">
                <b-link to="/stockfile">-股票建檔</b-link>
              </b-collapse>
              <b-collapse id="collapse-stock">
                <b-link to='/performance'>-近2年價量</b-link>
              </b-collapse>
            </div>
            
          </li>
        </ul>
      </div>
    </nav>
  
</template>

<script>
  import { HomeIcon,FileTextIcon,AwardIcon } from 'vue-feather-icons'
  export default {
    name:'Sidenav',
    components:{ HomeIcon,FileTextIcon,AwardIcon },
    data(){
      return {
      }
    }
  }
</script>

<style>

</style>