<script setup>
import {reactive, ref, watch} from "vue";
import {useRouter, useRoute} from "vue-router";

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
  <div class="content">
    <var-form ref="form" scroll-to-error="start">
      <var-space direction="column" justify="center">
        <var-input
          type="text"
          name="username"
          placeholder="用户名"
          :rules="[(v) => !!v || '用户名不能为空']"
          v-model="formData.username"
        />
        <var-input
          type="password"
          autocomplete="current-password"
          placeholder="密码"
          :rules="[(v) => !!v || '密码不能为空']"
          v-model="formData.password"
        />
        <var-button block type="primary" @click="login">
          登录
        </var-button>
      </var-space>
    </var-form>
  </div>

</template>

<style scoped>
.content {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
}

</style>
