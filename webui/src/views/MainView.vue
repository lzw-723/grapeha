<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { createAlova } from "alova";
import GlobalFetch from "alova/GlobalFetch";
import { getToken, getUsername } from "../store";

const alovaInstance = createAlova({
  requestAdapter: GlobalFetch(),
});
const router = useRouter();

const token = getToken();
const username = getUsername();
let books = ref([]);

async function getData() {
  alovaInstance
    .Get("http://localhost:8080/api/v1/books", {
      headers: {
        Authorization: "Bearer " + token.value,
      },
    })
    .then((response) => response.json())
    .then((data) => {
      console.log(data);
      books.value = data.data;
    });
}

function checkLogin() {
  if (token.value == null || username.value == null) {
    return router.push("/login");
  }
  alovaInstance
    .Get("http://localhost:8080/api/v1/users/" + username.value, {
      headers: {
        Authorization: "Bearer " + token.value,
      },
    })
    .then((response) => {
      if (response.status === 401) {
        router.push("/login");
      }
    });
}

function getCoverUrl(id) {
  return "http://localhost:8080/api/v1/books/" + id + "/cover";
}

checkLogin();
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
        :subtitle="book.path"
      />
    </div>
  </ul>
</template>
