<script setup>
import { reactive, ref, watch } from "vue";
import { useRouter, useRoute } from 'vue-router'
import { createAlova } from "alova";
import GlobalFetch from "alova/GlobalFetch";

const formData = reactive({
  username: "",
  password: "",
});
const alovaInstance = createAlova({
  requestAdapter: GlobalFetch(),
});

let succeed = ref(false);
let msg = ref("");
let token = ref("");

const router = useRouter();

watch(succeed, (value, oldValue) => {
  if (value) {
    router.push("/")
  }
})

async function login() {
  alovaInstance
    .Post("http://localhost:8080/api/v1/users/" + formData.username, {
      password: formData.password,
    })
    .then((response) => response.json())
    .then((data) => {
      console.log(data);
      if (data.code === 200) {
        msg.value = data.msg;
        succeed.value = true;
      }
    });
}
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
