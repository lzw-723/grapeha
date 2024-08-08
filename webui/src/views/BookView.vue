<script setup>
import {ref, watch} from "vue";
import {useRoute} from "vue-router";
import {fetchBookById, getBookCoverById} from "../api";

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
</script>

<style scoped>
.content {
  padding: 1rem;
}
</style>

<template>
  <var-app-bar>
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

    <template #right>
      <var-menu>
        <var-button
          color="transparent"
          text-color="#fff"
          round
          text
        >
          <var-icon name="menu" :size="24"/>
        </var-button>

        <template #menu>
          <var-cell ripple>选项卡</var-cell>
          <var-cell ripple>选项卡</var-cell>
          <var-cell ripple>选项卡</var-cell>
        </template>
      </var-menu>
    </template>
  </var-app-bar>


  <var-row class="content" :gutter="[10, 60]">
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
