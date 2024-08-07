<script setup>
import {ref} from "vue";
import {useRouter} from "vue-router";
import {getToken, getUsername} from "../store";
import {checkLogin, fetchBooks} from "../api";

const router = useRouter();

let books = ref([]);

async function getBooks() {
  try {
    books.value = await fetchBooks();
  } catch (err) {
  }
}

async function getBookShelves() {
  console.log("getBookShelves");
}

function getCoverUrl(id) {
  return "http://localhost:8080/api/v1/books/" + id + "/cover";
}

function goBook(id) {
  router.push("/books/" + id);
}

function goLogin() {
  router.push("/login");
}

function logout() {
  getUsername().value = "";
  getToken().value = "";
  router.push("/login");
}

checkLogin()
  .then((result) => {
    console.log("成功")
    console.log(result);
  })
  .catch((err) => {
    // console.error(err);
    goLogin();
  });

getBooks();
</script>

<template>
  <var-app-bar title="GrapeHA">
    <template #right>
      <var-button color="transparent"
                  text-color="#fff"
                  round
                  text
                  @click="logout">
        <var-icon name="power" :size="24"/>
      </var-button>
    </template>
  </var-app-bar>
  <!--  <var-card-->
  <!--    src="https://varletjs.org/cat.jpg"-->
  <!--    style="width: 368px; height: 260px; margin: 1rem"-->
  <!--    image-width="368"-->
  <!--    image-height="260"-->
  <!--    outline-->
  <!--    :elevation="0"-->
  <!--    title="宠物"-->
  <!--  />-->
  <ul>
    <li>
      <div style="width: 368px;height: 260px; display: flex; align-items: center;justify-content: center">
        <h2 style="text-align: center">标题</h2>
      </div>
    </li>
  </ul>

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
