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
  res_url.value = res;
  const label = content.value.find(item => item.content === res).title;
  title.value = book.value.name + " - " + label;
  fetch(res_url.value).then(r => r.text()).then(t => {
    html.value = t;
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
    readResource(r[0].content, r[0].title);
  });
}, {immediate: true});
</script>

<template>
  <Appbar :title @content="openMenu = true"/>
  <sl-drawer label="Drawer" class="drawer-focus" :open="openMenu" @sl-after-hide="openMenu = false">
    <sl-menu>
      <sl-menu-item v-for="item in content" @click="readResource(item.content)">{{
          item.title
        }}
      </sl-menu-item>
    </sl-menu>
  </sl-drawer>

  <Reader :src="res_url"/>
  <!--  <iframe class="read-area" :srcdoc="html"></iframe>-->
  <!--  <sl-include :src="res_url">-->
  <!--  </sl-include>-->
</template>

<style scoped>
</style>
