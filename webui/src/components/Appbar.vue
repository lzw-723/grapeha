<script setup>
import {watch} from "vue";
import {useRoute, useRouter} from "vue-router";
import '@shoelace-style/shoelace/dist/components/button/button.js';
import '@shoelace-style/shoelace/dist/components/button-group/button-group.js';
import {getToken, getUsername} from "../store.js";

defineEmits(["menu", "content"])
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
    <sl-button-group v-if="route.path !== '/login'">
      <sl-button v-if="route.path !== '/'" @click="$router.back()">返回</sl-button>
      <sl-button @click="$router.push('/')">主页</sl-button>
    </sl-button-group>
    <h1>{{ props.title }}</h1>
    <sl-button-group>
      <sl-button v-show="route.path === '/'" @click="logout">退出</sl-button>
      <sl-button v-show="route.path.startsWith('/read')" @click="$emit('menu')">菜单</sl-button>
      <sl-button v-show="route.path.startsWith('/read')" @click="$emit('content')">目录</sl-button>
    </sl-button-group>
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

.app-bar sl-button-group {
  flex-grow: 0.2;
}

</style>
