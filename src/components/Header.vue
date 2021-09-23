<template>
  <div>
    <b-navbar id="navbar" class="navbar fixed-top mx-1 justify-content-between" variant="success" type="dark">
      <b-navbar-brand>CHicZU 財務管理平台</b-navbar-brand>
      <button class="navbar-toggler position-absolute d-md-none collapsed" type="button" data-toggle="collapse" data-target="#sidebarMenu" aria-controls="sidebarMenu" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
      </button>
      <b-navbar-nav class="ml-auto">
        <img id="navbar_img" class="img-md rounded-circle" src="../assets/images/faces-clipart/face8.jpg" alt="Profile image"> 
        <b-nav-item-dropdown right>
          <img class="img-md rounded-circle" src="../assets/images/faces-clipart/pic-1.png" alt="Profile image">
          <p class="mb-2 mt-2 font-weight-semibold">Allen Moreno</p>
          <b-dropdown-item href="/">回首頁</b-dropdown-item>
          <b-dropdown-item href="#">退出</b-dropdown-item>
        </b-nav-item-dropdown>
      </b-navbar-nav>
    </b-navbar>
  </div>
</template>

<script>
export default {

}
</script>

<style>
  #navbar {
    padding: 20px;
    height: 50px;
  }  
  #navbar_img {
    padding: 5px;
    height: 50px;
  }
</style>