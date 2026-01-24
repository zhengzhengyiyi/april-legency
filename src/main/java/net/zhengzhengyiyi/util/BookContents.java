package net.zhengzhengyiyi.util;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.RawFilteredPair;
import net.minecraft.text.Text;
import java.util.List;
import java.util.Optional;

public interface BookContents {
    BookContents EMPTY = new BookContents() {
        @Override
        public int getPageCount() { return 0; }
        @Override
        public Text getPage(int index) { return Text.empty(); }
    };

    int getPageCount();

    Text getPage(int index);

    default Text getPageOrDefault(int index) {
        return (index >= 0 && index < this.getPageCount()) ? this.getPage(index) : Text.empty();
    }

    static BookContents create(ItemStack stack) {
        if (stack.isOf(Items.WRITTEN_BOOK)) {
            return new WrittenBookContents(stack);
        } else if (stack.isOf(Items.WRITABLE_BOOK)) {
            return new WritableBookContents(stack);
        }
        return EMPTY;
    }

    record WritableBookContents(List<String> pages) implements BookContents {
        public WritableBookContents(ItemStack stack) {
            this(Optional.ofNullable(stack.get(DataComponentTypes.WRITABLE_BOOK_CONTENT))
                    .map(content -> content.pages().stream().map(RawFilteredPair::raw).toList())
                    .orElse(List.of()));
        }

        @Override
        public int getPageCount() { return pages.size(); }

        @Override
        public Text getPage(int index) { return Text.literal(pages.get(index)); }
    }

    record WrittenBookContents(List<Text> pages) implements BookContents {
        public WrittenBookContents(ItemStack stack) {
            this(Optional.ofNullable(stack.get(DataComponentTypes.WRITTEN_BOOK_CONTENT))
                    .map(content -> content.pages().stream().map(RawFilteredPair::raw).toList())
                    .orElse(List.of()));
        }

        @Override
        public int getPageCount() { return pages.size(); }

        @Override
        public Text getPage(int index) { return pages.get(index); }
    }
}
