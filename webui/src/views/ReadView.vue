<script setup>

import {ref, watch} from "vue";
import {fetchBookById, fetchBookContent, getBookResourceById} from "../api.js";
import {useRoute} from "vue-router";

const route = useRoute();
const book = ref({});
const content = ref([]);
const res_url = ref("");
const css = ref("");
const html = ref("");

const title = ref("");

const showAppBar = ref(true);
const showContent = ref(false);

function getContent() {
  fetchBookContent().then(r => content.value = r).catch(err => console.log(err));
}

function readResource(res, label) {
  console.log(label);
  console.log(res);
  // res_url.value = window.location.origin+ res;
  let isXhtml = res.endsWith(".xhtml");
  res_url.value = "http://localhost:8080" + res;
  title.value = book.value.name + " - " + label;
  fetch(res_url.value).then(r => r.text()).then(t => {
    let base = res_url.value.substring(0, res_url.value.lastIndexOf("/") + 1);
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
  <var-app-bar :title="title">
    <template #left>
      <var-button
        color="transparent"
        text-color="#fff"
        round
        text
        @click="$router.back()"
      >
        <var-icon name="chevron-left" :size="24"/>
      </var-button>
      <var-button
        color="transparent"
        text-color="#fff"
        round
        text
        @click="$router.push('/')"
      >
        <var-icon name="home" :size="24"/>
      </var-button>
    </template>
  </var-app-bar>
  <var-popup position="right" v-model:show="showContent">
    <ol>
      <var-cell v-for="item in content">
        <var-link @click="readResource(item.href, item.label)">{{ item.label }}</var-link>
      </var-cell>
    </ol>
  </var-popup>
  <var-fab type="default" inactive-icon="format-list-checkbox" @click="showContent = !showContent"/>

  <iframe class="read-area" :srcdoc="html"></iframe>
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
