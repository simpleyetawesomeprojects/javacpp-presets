package sample;


import org.bytedeco.hyperscan.ch_capture_t;
import org.bytedeco.hyperscan.ch_compile_error_t;
import org.bytedeco.hyperscan.ch_database_t;
import org.bytedeco.hyperscan.ch_error_event_handler;
import org.bytedeco.hyperscan.ch_match_event_handler;
import org.bytedeco.hyperscan.ch_scratch_t;
import org.bytedeco.hyperscan.global.hyperscan;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.javacpp.annotation.Cast;
import org.bytedeco.javacpp.annotation.Const;

import java.util.LinkedList;

import static org.bytedeco.hyperscan.global.hyperscan.HS_FLAG_SINGLEMATCH;
import static org.bytedeco.hyperscan.global.hyperscan.HS_MODE_BLOCK;


public class ChScanExample {

    public static void main(String... args) {
        Loader.load(hyperscan.class);

        String[] patterns = {"abc1", "asa", "dab"};
        ch_database_t database_t = null;
        ch_match_event_handler matchEventHandler = null;
        ch_error_event_handler errorEventHandler = null;
        ch_scratch_t scratchSpace = new ch_scratch_t();
        ch_compile_error_t compile_error_t;
        final LinkedList<long[]> matchedIds = new LinkedList<>();
        try (PointerPointer<ch_database_t> database_t_p = new PointerPointer<>(1);
             PointerPointer<ch_compile_error_t> compile_error_t_p = new PointerPointer<>(1);
             IntPointer compileFlags = new IntPointer(HS_FLAG_SINGLEMATCH, HS_FLAG_SINGLEMATCH, HS_FLAG_SINGLEMATCH);
             IntPointer patternIds = new IntPointer(1, 2, 3);
             PointerPointer expressionsPointer = new PointerPointer<BytePointer>(patterns);
        ) {
            matchEventHandler = new ch_match_event_handler() {
                @Override
                public int call(@Cast("unsigned int") int id,
                                @Cast("unsigned long long") long from,
                                @Cast("unsigned long long") long to,
                                @Cast("unsigned int") int flags,
                                @Cast("unsigned int") int size,
                                @Const ch_capture_t captured,
                                Pointer ctx) {

                    System.out.println("id"+id+": "+from + "-" + to);
                    matchedIds.add(new long[]{from, to});
                    return 0;
                }
            };

            errorEventHandler = new ch_error_event_handler(){
                @Override
                public int call(@Cast("ch_error_event_t") int error_type,
                                @Cast("unsigned int") int id, Pointer info,
                                Pointer ctx) {

                    System.out.println("Error for id"+id+":" + error_type);
                    return 0;
                }
            };

            int result = hyperscan.ch_compile_multi(
                    expressionsPointer,
                    compileFlags,
                    patternIds,
                    3,
                    HS_MODE_BLOCK,
                    null,
                    database_t_p,
                    compile_error_t_p
            );

            database_t = new ch_database_t(database_t_p.get(0));
            compile_error_t = new ch_compile_error_t(compile_error_t_p.get(0));
            if (result != 0) {
                System.out.println(compile_error_t.message().getString());
                System.exit(1);
            }
            result = hyperscan.ch_alloc_scratch(database_t, scratchSpace);
            if (result != 0) {
                System.out.println("Error during scratch space allocation");
                System.exit(1);
            }

            String textToSearch = "-21dasaaadabcaaa";

            hyperscan.ch_scan(
                    database_t,
                    textToSearch,
                    textToSearch.length(),
                    0,
                    scratchSpace,
                    matchEventHandler,
                    errorEventHandler,
                    expressionsPointer
            );

            System.out.println("Count: "+matchedIds.size());

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            hyperscan.ch_free_scratch(scratchSpace);
            if (database_t != null) {
                hyperscan.ch_free_database(database_t);
            }
            if (matchEventHandler != null) {
                matchEventHandler.close();
            }
        }
    }
}
