<script setup>
import {reactive, ref, watch} from "vue";
import {useRouter, useRoute} from "vue-router";
import '@shoelace-style/shoelace/dist/components/button/button.js';
import '@shoelace-style/shoelace/dist/components/input/input.js';

import {getToken, getUsername} from "../store";
import {checkLogin, fetchUserToken} from "../api.js";

const formData = reactive({
  username: "",
  password: "",
});

const token = getToken();
const username = getUsername();

let succeed = ref(false);

const router = useRouter();

watch(succeed, (value) => {
  if (value) {
    router.back();
  }
});

function login() {
  fetchUserToken(formData.username, formData.password)
    .then((t) => {
        username.value = formData.username;
        token.value = t;
        succeed.value = true;
      }
    );
}

checkLogin().then(r => succeed.value = true)
</script>

<template>

  <form ref="form">
    <sl-input
      type="text"
      name="username"
      placeholder="用户名"
      @slInput="formData.username = $event.target.value"
    />
    <sl-input
      type="password"
      autocomplete="current-password"
      placeholder="密码"
      @slInput="formData.password = $event.target.value"
    />
    <sl-button @click="login">
      登录
    </sl-button>
  </form>

</template>
