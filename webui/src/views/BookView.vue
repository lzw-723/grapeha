<script setup>
import {ref, watch} from "vue";
import {useRoute, useRouter} from "vue-router";
import '@shoelace-style/shoelace/dist/components/button/button.js';
import '@shoelace-style/shoelace/dist/components/badge/badge.js';
import {fetchBookById, getBookCoverById} from "../api";
import Appbar from "../components/Appbar.vue";

const route = useRoute();
const router = useRouter();
let book = ref({
  id: null,
  title: null,
  cover: null,
  author: null,
  description: null,
  file: null,
});

watch(
  () => route.params.id,
  (v) => {
    console.log(v);
    fetchBookById(v)
      .then((result) => {
        console.log(result);
        book.value.id = result.id;
        book.value.title = result.name;
        book.value.cover =
          getBookCoverById(result.id);
        book.value.author = result.author;
        book.value.description = result.description;
        book.value.file = result.path;
      })
      .catch((err) => {
      });
  },
  {immediate: true}
);

function goRead(id) {
  router.push("/read/" + id);
}
</script>

<style scoped>
.content {
  padding: 1rem;
}
</style>

<template>
  <Appbar :title="book.title"/>
  <div class="content">
    <img
      :src="book.cover"
      class="cover"
      alt="cover"/>

    <div>
      <div class="title">
        <h1>{{ book.title }}</h1>
        <div>
          <sl-button @click="goRead(book.id)">阅读</sl-button>
          <sl-button>收藏</sl-button>
        </div>
      </div>

      <p>
        <sl-badge>{{ book.author }}</sl-badge>
        <!--        <sl-badge>{{ book.file }}</sl-badge>-->
      </p>

      <!-- <var-cell>文件大小：</var-cell>
      <var-cell>文件格式：</var-cell>
      <var-cell>文件来源：</var-cell> -->
      <p>
        {{ book.description || "暂无介绍" }}
      </p>
    </div>
  </div>
</template>


<style scoped>

.content {
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  gap: 1rem;
  margin: 4px;
}

.title {
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  gap: 1rem;
  align-items: center;
}

.title > h1 {
  flex-grow: 1;
}

.cover {
  width: 100%;
  max-width: min(300px, 100%);
  height: auto;
  max-height: 80vh;
}

.content > div {
  width: max(calc(100% - 340px), 300px);
}
</style>
