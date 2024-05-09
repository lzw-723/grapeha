<script setup>
import { ref } from 'vue';
import { createAlova } from 'alova';
import GlobalFetch from 'alova/GlobalFetch';

const alovaInstance = createAlova({
  requestAdapter: GlobalFetch()
});

let books = ref([]);

async function getData() {
  // const response = await alovaInstance.Get('http://localhost:8080/api/v1/book');
  // console.log(response);
  alovaInstance
    .Get('http://localhost:8080/api/v1/books')
    .then(response => response.json())
    .then(data => {
      console.log(data);
      books.value = data.data;
    });
}

function getCoverUrl(id) {
  return "http://localhost:8080/api/v1/books/" + id + "/cover";
}
</script>

<template>
  <var-app-bar title="标题" />
  <var-button @click="getData">GET</var-button>
  <ul style="display: flex; flex-direction: row;flex-wrap: wrap;">
    <div v-for="book in books" style="width: 200px; height: 300px; margin: 1rem;">
      <!-- <var-image width="100px" height="160px" fit="cover" :src="getCoverUrl(book.id)" alt="" srcset="" /> -->
      <var-card :src="getCoverUrl(book.id)" :title="book.name" :subtitle="book.path" />
    </div>
  </ul>
</template>
