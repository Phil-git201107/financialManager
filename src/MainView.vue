<template>
   <div>
     <div class="container-fluid">
     <Header></Header>
      <div class="row">
        <Sidenav />
        <main role="main" class="col-md-9 ml-sm-auto col-lg-10 mt-5 px-md-4">
          <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
            <router-view></router-view>
          </div>
        </main>
      </div>
    </div>
   </div>
   
</template>

<script>
  import Header from './components/Header.vue'
  import Sidenav from './components/Sidenav.vue'

  export default {
    name:'MainView',
    components: {Header,Sidenav}
  }
</script>

<style>
  .bd-placeholder-img {
    font-size: 1.125rem;
    text-anchor: middle;
    -webkit-user-select: none;
    -moz-user-select: none;
    -ms-user-select: none;
    user-select: none;
  }

  @media (min-width: 768px) {
    .bd-placeholder-img-lg {
      font-size: 3.5rem;
      }
  }
</style>