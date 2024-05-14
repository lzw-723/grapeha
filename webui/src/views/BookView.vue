<script setup>
import { ref, watch } from "vue";
import { useRoute } from "vue-router";
import { fetchBookById } from "../api";

const route = useRoute();
let book = ref({
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
        book.value.title = result.name;
        book.value.cover =
          "http://localhost:8080/api/v1/books/" + result.id + "/cover";
        book.value.author = result.author;
        book.value.description = result.description;
        book.value.file = result.path;
      })
      .catch((err) => {});
  },
  { immediate: true }
);
</script>

<template>
  <var-row :gutter="[10, 60]">
    <var-col :span="5">
      <div class="cover">
        <var-image
          width="300px"
          height="400px"
          radius="10"
          lazy
          fit="cover"
          :src="book.cover"
        />
      </div>
    </var-col>
    <var-col :span="16">
      <ul class="details">
        <var-cell
          ><h1>{{ book.title }}</h1></var-cell
        >

        <var-cell>
          <var-space>
            <var-chip>{{ book.author }}</var-chip>
            <var-chip plain>{{ book.file }}</var-chip>
          </var-space>
        </var-cell>
        <!-- <var-cell>文件大小：</var-cell>
        <var-cell>文件格式：</var-cell>
        <var-cell>文件来源：</var-cell> -->
        <var-cell>
          <!-- style="max-width: 170px" -->
          <var-ellipsis expand-trigger="click" :line-clamp="3" :tooltip="false">
            {{ book.description || "暂无" }}
          </var-ellipsis>
        </var-cell>
        <var-space :size="[10, 10]">
          <var-button text outline type="primary">阅读</var-button>
          <var-button text type="primary">收藏</var-button>
        </var-space>
      </ul>
    </var-col>
  </var-row>
</template>
