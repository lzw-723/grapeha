<script setup>
import {ref} from "vue";
import {useRouter} from "vue-router";
import {checkLogin, fetchBooks, fetchBookshelves} from "../api";
import Bookshelf from "../components/Bookshelf.vue";
import Book from "../components/Book.vue";
import Appbar from "../components/Appbar.vue";

const router = useRouter();

let bookshelves = ref([]);
let books = ref([]);

function getBookshelves() {
  fetchBookshelves().then(r => bookshelves.value = r).catch(err => console.log(err));
}

async function getBooks() {
  try {
    books.value = await fetchBooks();
  } catch (err) {
    console.log("获取books失败");
  }
}

async function getBookShelves() {
  console.log("getBookShelves");
}

function getCoverUrl(id) {
  return "/api/v1/books/" + id + "/cover";
}

function goBookshelf(id) {
  router.push("/bookshelves/" + id);
}

function goBook(id) {
  router.push("/books/" + id);
}

function goLogin() {
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

getBookshelves();
getBooks();
</script>

<template>
  <Appbar title="主页"/>

  <sl-divider></sl-divider>


  <ul class="flex flex-row overflow-scroll">
      <Bookshelf
        class="flex-auto" v-for="bookshelf in bookshelves"
        :title="bookshelf.title"
        @click="goBookshelf(bookshelf.id)">
      </Bookshelf>
  </ul>
  <sl-divider></sl-divider>

  <ul class="book-list">
    <Book v-for="book in books"
          class=""
          :cover="getCoverUrl(book.id)"
          :title="book.name"
          :author="book.author"
          @click="goBook(book.id)"
    />
  </ul>
</template>

<style>
.book-list {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
}
</style>
