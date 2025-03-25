<script setup>
import {defineProps, defineEmits, watch} from "vue";
import {useRoute, useRouter} from "vue-router";
import {getToken, getUsername} from "../store.js";

defineEmits(["content"])
const props = defineProps(
  {
    title: {
      type: String,
      default: "grapeha",
    }
  }
)

const router = useRouter();
const route = useRoute();

function logout() {
  getUsername().value = "";
  getToken().value = "";
  router.push("/login");
}

// 设置head标题
watch(
  () => props.title,
  (v) => {
    document.title = "grapeha - " + props.title;
  },
  {immediate: true}
)
</script>

<template>
  <div class="app-bar">
    <div v-if="route.path !== '/login'">
      <sl-button @click="$router.back()">返回</sl-button>
      <sl-button @click="$router.push('/')">主页</sl-button>
    </div>
    <h1>{{ props.title }}</h1>
    <div>
      <sl-button v-show="route.path === '/'" @click="logout">退出</sl-button>
      <sl-button v-show="route.path === '/'">菜单</sl-button>
      <sl-button v-show="route.path.startsWith('/read')" @click="$emit('content')">目录</sl-button>
    </div>
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

.app-bar div {
  flex-grow: 0.2;
  display: flex;
  justify-items: end;
}

.app-bar div:last-child {
  flex-direction: row-reverse;
}

</style>
