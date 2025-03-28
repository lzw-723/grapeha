<script setup>

import {ref, watch} from "vue";
import "@shoelace-style/shoelace/dist/components/drawer/drawer.js";
import "@shoelace-style/shoelace/dist/components/menu/menu.js";
import "@shoelace-style/shoelace/dist/components/menu-item/menu-item.js";
import "@shoelace-style/shoelace/dist/components/button/button.js";
import "@shoelace-style/shoelace/dist/components/include/include.js";
import {fetchBookById, fetchBookContent} from "../api.js";
import {useRoute} from "vue-router";
import Appbar from "../components/Appbar.vue";
import Reader from "../components/Reader.vue";

const route = useRoute();
const book = ref({});
const content = ref([]);
const res_url = ref("");
const html = ref("");

const title = ref("");

const openMenu = ref(false);

function readResource(res) {
  console.log(res);
  const label = content.value.find(item => item.content === res)?.title || "未命名";
  title.value = book.value.name + " - " + label;
  // 提取锚点
  let anchor = null;
  if (res.includes("#")) {
    anchor = "#" + res.split("#")[1];
    res = res.split("#")[0];
  }
  res_url.value = res;
  fetch(res_url.value).then(r => r.text()).then(t => {
    html.value = t;
    if (anchor) {
      const element = document.querySelector(anchor);
      if (element) {
        element.scrollIntoView({behavior: "smooth"});
      }
    } else {
      // 滚动到顶部
      window.scrollTo({
        top: 0,
        behavior: "smooth",
      })
    }
  }).catch(err => {
    console.log(err);
  })
}

function next() {
  const index = content.value.findIndex(item => item.content === res_url.value);
  if (index < content.value.length - 1) {
    readResource(content.value[index + 1].content);
  }
}

watch(() => route.params.id, (id) => {
  fetchBookById(id).then(r => {
    book.value = r;
  });
  fetchBookContent(id).then(r => {
    console.log(r);
    content.value = r;
    readResource(r[0].content);
  });
}, {immediate: true});
</script>

<template>
  <Appbar :title @content="openMenu = true"/>
  <sl-drawer label="目录" :open="openMenu" @sl-after-hide="openMenu = false">
    <sl-menu>
      <sl-menu-item v-for="item in content" @click="readResource(item.content)">{{ item.title }}</sl-menu-item>
    </sl-menu>
  </sl-drawer>

  <Reader :src="res_url" @goto="readResource($event)" @next="next"/>
  <!--  <iframe class="read-area" :srcdoc="html"></iframe>-->
  <!--  <sl-include :src="res_url">-->
  <!--  </sl-include>-->
</template>

<style scoped>
</style>
