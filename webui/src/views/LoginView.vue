<script setup>
import {reactive, ref, watch} from "vue";
import {useRouter, useRoute} from "vue-router";
import '@shoelace-style/shoelace/dist/components/button/button.js';
import '@shoelace-style/shoelace/dist/components/input/input.js';

import {checkLogin, fetchUserToken} from "../api.js";
import Appbar from "../components/Appbar.vue";

const username = ref("");
const password = ref("");

let succeed = ref(false);

const router = useRouter();

watch(succeed, (value) => {
  if (value) {
    router.back();
  }
});

function login() {
  fetchUserToken(username.value, password.value)
    .then((t) => {
        succeed.value = true;
      }
    );
}

checkLogin().then(r => succeed.value = true)
</script>

<template>
  <Appbar/>
  <div class="top-padding">

  </div>
  <form class="login-form">
    <sl-input
      type="text"
      name="username"
      placeholder="用户名"
      v-model="username"
    />
    <sl-input
      type="password"
      autocomplete="current-password"
      placeholder="密码"
      v-model="password"
    />
    <sl-button class="login-button" @click="login">
      登录
    </sl-button>
  </form>

</template>

<style scoped>
.top-padding {
  height: 10rem;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 6px;
  justify-content: center;
  align-items: center;
}

</style>
