<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { fetchBooks, checkLogin } from "../api";

const router = useRouter();

let books = ref([]);

function getData() {
  return fetchBooks()
    .then((result) => {
      books.value = result;
    })
    .catch((err) => {});
}

function getCoverUrl(id) {
  return "http://localhost:8080/api/v1/books/" + id + "/cover";
}

function goBook(id) {
  router.push("/books/" + id);
}

checkLogin()
  .then((result) => {
    console.log(result);
  })
  .catch((err) => {
    console.error(err);
  });
</script>

<template>
  <var-app-bar title="标题" />
  <var-button @click="getData">GET</var-button>
  <ul style="display: flex; flex-direction: row; flex-wrap: wrap">
    <div
      v-for="book in books"
      style="width: 200px; height: 300px; margin: 1rem"
    >
      <!-- <var-image width="100px" height="160px" fit="cover" :src="getCoverUrl(book.id)" alt="" srcset="" /> -->
      <var-card
        :src="getCoverUrl(book.id)"
        :title="book.name"
        :subtitle="book.author"
        @click="goBook(book.id)"
      />
    </div>
  </ul>
</template>
