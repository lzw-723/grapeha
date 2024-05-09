<script setup>
import MainView from './views/MainView.vue';
import LoginView from './views/LoginView.vue';
</script>

<template>
  <!-- <MainView></MainView> -->
  <!-- <LoginView></LoginView> -->
  <router-view></router-view>
</template>

<style scoped>
.logo {
  height: 6em;
  padding: 1.5em;
  will-change: filter;
  transition: filter 300ms;
}

.logo:hover {
  filter: drop-shadow(0 0 2em #646cffaa);
}

.logo.vue:hover {
  filter: drop-shadow(0 0 2em #42b883aa);
}
</style>
