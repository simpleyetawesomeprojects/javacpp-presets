// Targeted by JavaCPP version 1.5.8-SNAPSHOT: DO NOT EDIT THIS FILE

package org.bytedeco.pytorch;

import org.bytedeco.pytorch.Allocator;
import org.bytedeco.pytorch.Function;
import org.bytedeco.pytorch.Module;
import java.nio.*;
import org.bytedeco.javacpp.*;
import org.bytedeco.javacpp.annotation.*;

import static org.bytedeco.javacpp.presets.javacpp.*;
import static org.bytedeco.openblas.global.openblas_nolapack.*;
import static org.bytedeco.openblas.global.openblas.*;

import static org.bytedeco.pytorch.global.torch.*;
 // namespace ivalue

// This is an owning wrapper for a c10::optional<std::vector<T>>
// that can be implicitly converted to a (non-owning) optional<ArrayRef<T>>.
// Its purpose is to be used in generated code to keep the vector alive
// either until the end of a statement (as a temporary), or as a saved arg
// in autograd.
@Name("c10::OptionalArray<int64_t>") @NoOffset @Properties(inherit = org.bytedeco.pytorch.presets.torch.class)
public class LongOptionalArray extends Pointer {
    static { Loader.load(); }
    /** Pointer cast constructor. Invokes {@link Pointer#Pointer(Pointer)}. */
    public LongOptionalArray(Pointer p) { super(p); }
    /** Native array allocator. Access with {@link Pointer#position(long)}. */
    public LongOptionalArray(long size) { super((Pointer)null); allocateArray(size); }
    private native void allocateArray(long size);
    @Override public LongOptionalArray position(long position) {
        return (LongOptionalArray)super.position(position);
    }
    @Override public LongOptionalArray getPointer(long i) {
        return new LongOptionalArray((Pointer)this).offsetAddress(i);
    }

  public native @ByRef LongVectorOptional list(); public native LongOptionalArray list(LongVectorOptional setter);

  public LongOptionalArray() { super((Pointer)null); allocate(); }
  private native void allocate();
  public LongOptionalArray(@ByVal @Cast("std::vector<int64_t>*") LongVector val) { super((Pointer)null); allocate(val); }
  private native void allocate(@ByVal @Cast("std::vector<int64_t>*") LongVector val);

  // Used when saving an argument for the backwards pass.
  public native @ByRef @Name("operator =") LongOptionalArray put(@ByVal LongArrayRefOptional ref);

  public native @ByVal @Name("operator c10::optional<c10::ArrayRef<int64_t> >") LongArrayRefOptional asLongArrayRefOptional();
}
