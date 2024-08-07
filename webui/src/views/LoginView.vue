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
let msg = ref("");

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
  <var-snackbar v-model:show="succeed">{{ msg }}</var-snackbar>
  <var-row>
    <var-col :span="16" :offset="4">
      <div>
        <var-form ref="form" scroll-to-error="start">
          <var-space direction="column" justify="center">
            <var-input
              name="username"
              autocomplete="username"
              placeholder="用户名"
              :rules="[(v) => !!v || '用户名不能为空']"
              v-model="formData.username"
            />
            <var-input
              type="password"
              placeholder="密码"
              :rules="[(v) => !!v || '密码不能为空']"
              v-model="formData.password"
            />
            <var-space direction="column" :size="[14, 0]">
              <var-button block type="primary" @click="login">
                登录
              </var-button>
            </var-space>
          </var-space>
        </var-form>
      </div>
    </var-col>
  </var-row>
</template>
