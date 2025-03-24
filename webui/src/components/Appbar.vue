<script setup>

import {useRoute, useRouter} from "vue-router";
import {getToken, getUsername} from "../store.js";

const props = defineProps(
  {
    title: String,
  }
)

const router = useRouter();
const route = useRoute();

function logout() {
  getUsername().value = "";
  getToken().value = "";
  router.push("/login");
}
</script>

<template>
  <div class="app-bar">
    <sl-button @click="$router.back()">返回</sl-button>
    <sl-button @click="$router.push('/')">主页</sl-button>
    <h1>{{props.title}}</h1>
    <sl-button v-if="route.path === '/'" @click="logout">退出</sl-button>
  </div>
</template>

<style scoped>
.app-bar {
  display: flex;
  flex-direction: row;
  align-items: center;
  padding: 10px;
}

.app-bar h1 {
  flex-grow: 1;
  margin: 0;
  padding: 0;
  text-align: center;
  font-size: 1rem;
}

</style>
