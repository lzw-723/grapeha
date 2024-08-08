<script setup>

import {ref, watch} from "vue";
import {fetchBookById, fetchBookContent, getBookResourceById} from "../api.js";
import {useRoute} from "vue-router";

const route = useRoute();
const book = ref({});
const content = ref([]);
const res_url = ref("");

const title = ref("");

const showAppBar = ref(true);
const showContent = ref(false);

function getContent() {
  fetchBookContent().then(r => content.value = r).catch(err => console.log(err));
}

function readResource(res, label) {
  res_url.value = getBookResourceById(book.value.id, res);
  title.value = book.value.name + " - " + label;
}

watch(() => route.params.id, (id) => {
  fetchBookById(id).then(r => {
    book.value = r;
  });
  fetchBookContent(id).then(r => {
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
  <iframe class="read-area"
          :src="res_url">

  </iframe>

</template>

<style scoped>
.read-area {
  width: 100%;
  height: 82vh;
  border-width: 0;
  margin: 1rem;
}

</style>
