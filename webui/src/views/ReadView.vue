<script setup>

import {ref, watch} from "vue";
import "@shoelace-style/shoelace/dist/components/drawer/drawer.js";
import "@shoelace-style/shoelace/dist/components/menu/menu.js";
import "@shoelace-style/shoelace/dist/components/menu-item/menu-item.js";
import "@shoelace-style/shoelace/dist/components/button/button.js";
import "@shoelace-style/shoelace/dist/components/include/include.js";
import {fetchBookById, fetchBookContent, getBookResourceById} from "../api.js";
import {useRoute} from "vue-router";
import Appbar from "../components/Appbar.vue";

const route = useRoute();
const book = ref({});
const content = ref([]);
const res_url = ref("");
const css = ref("");
const html = ref("");

const title = ref("");

const openMenu = ref(false);

function getContent() {
  fetchBookContent().then(r => content.value = r).catch(err => console.log(err));
}

function readResource(res, label) {
  console.log(label);
  console.log(res);
  // res_url.value = window.location.origin+ res;
  let isXhtml = res.endsWith(".xhtml");
  res_url.value = res;
  title.value = book.value.name + " - " + label;
  fetch(res_url.value).then(r => r.text()).then(t => {
    let base = window.location.origin + res;
    const h = new DOMParser().parseFromString(t, isXhtml ? "application/xhtml+xml" : "text/html");
    // 处理图片
    let imgs = h.getElementsByTagName("img");
    for (let i = 0; i < imgs.length; i++) {
      let url = new URL(imgs[i].getAttribute("src"), base);
      imgs[i].setAttribute("src", url.toString())
      console.log(imgs[i].src);
    }
    // 处理css
    let links = h.getElementsByTagName("link");
    for (let i = 0; i < links.length; i++) {
      links[i].href = new URL(links[i].getAttribute("href"), base);
    }
    let vue = h.createElement("script");
    vue.src = "https://unpkg.com/petite-vue";
    h.head.appendChild(vue);

    let script = h.createElement("script");
    script.text = "PetiteVue.createApp({ count: 1}).mount('#doc');console.log('hello world');"
    h.head.appendChild(script);

    html.value = h.documentElement.innerHTML;
  }).catch(err => {
    console.log(err);
  })
}

watch(() => route.params.id, (id) => {
  fetchBookById(id).then(r => {
    book.value = r;
  });
  fetchBookContent(id).then(r => {
    console.log(r);
    content.value = r;
    readResource(r[0].href, r[0].label);
  });
}, {immediate: true});
</script>

<template>
  <Appbar/>
  <sl-button class="menu-button" @click="openMenu = !openMenu">目录</sl-button>
  <sl-drawer label="Drawer" class="drawer-focus" :open="openMenu" @sl-after-hide="openMenu = false">
    <sl-button slot="footer" variant="primary" @click="openMenu = !openMenu">Close</sl-button>
    <sl-menu>
      <sl-menu-item v-for="item in content" @click="readResource(item.content, item.title)">{{ item.title }}</sl-menu-item>
    </sl-menu>
  </sl-drawer>

  <iframe class="read-area" :srcdoc="html"></iframe>
  <sl-include :src="res_url"></sl-include>
</template>

<style scoped>
.read-area {
  width: 100%;
  height: 82vh;
  border-width: 0;
  margin: 1rem;
}

.read-area > * {
  background: red;
}

</style>
