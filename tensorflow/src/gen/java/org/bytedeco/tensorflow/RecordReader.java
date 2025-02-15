// Targeted by JavaCPP version 1.5.7: DO NOT EDIT THIS FILE

package org.bytedeco.tensorflow;

import org.bytedeco.tensorflow.Allocator;
import java.nio.*;
import org.bytedeco.javacpp.*;
import org.bytedeco.javacpp.annotation.*;

import static org.bytedeco.javacpp.presets.javacpp.*;

import static org.bytedeco.tensorflow.global.tensorflow.*;


// Low-level interface to read TFRecord files.
//
// If using compression or buffering, consider using SequentialRecordReader.
//
// Note: this class is not thread safe; external synchronization required.
@Namespace("tensorflow::io") @NoOffset @Properties(inherit = org.bytedeco.tensorflow.presets.tensorflow.class)
public class RecordReader extends Pointer {
    static { Loader.load(); }
    /** Pointer cast constructor. Invokes {@link Pointer#Pointer(Pointer)}. */
    public RecordReader(Pointer p) { super(p); }

  // Format of a single record:
  //  uint64    length
  //  uint32    masked crc of length
  //  byte      data[length]
  //  uint32    masked crc of data
  @MemberGetter public static native @Cast("const size_t") long kHeaderSize();
  public static final long kHeaderSize = kHeaderSize();
  @MemberGetter public static native @Cast("const size_t") long kFooterSize();
  public static final long kFooterSize = kFooterSize();

  // Statistics (sizes are in units of bytes)
  public static class Stats extends Pointer {
      static { Loader.load(); }
      /** Default native constructor. */
      public Stats() { super((Pointer)null); allocate(); }
      /** Native array allocator. Access with {@link Pointer#position(long)}. */
      public Stats(long size) { super((Pointer)null); allocateArray(size); }
      /** Pointer cast constructor. Invokes {@link Pointer#Pointer(Pointer)}. */
      public Stats(Pointer p) { super(p); }
      private native void allocate();
      private native void allocateArray(long size);
      @Override public Stats position(long position) {
          return (Stats)super.position(position);
      }
      @Override public Stats getPointer(long i) {
          return new Stats((Pointer)this).offsetAddress(i);
      }
  
    public native @Cast("tensorflow::int64") long file_size(); public native Stats file_size(long setter);
    public native @Cast("tensorflow::int64") long data_size(); public native Stats data_size(long setter);
    public native @Cast("tensorflow::int64") long entries(); public native Stats entries(long setter);  // Number of values
  }

  // Metadata for the TFRecord file.
  public static class Metadata extends Pointer {
      static { Loader.load(); }
      /** Default native constructor. */
      public Metadata() { super((Pointer)null); allocate(); }
      /** Native array allocator. Access with {@link Pointer#position(long)}. */
      public Metadata(long size) { super((Pointer)null); allocateArray(size); }
      /** Pointer cast constructor. Invokes {@link Pointer#Pointer(Pointer)}. */
      public Metadata(Pointer p) { super(p); }
      private native void allocate();
      private native void allocateArray(long size);
      @Override public Metadata position(long position) {
          return (Metadata)super.position(position);
      }
      @Override public Metadata getPointer(long i) {
          return new Metadata((Pointer)this).offsetAddress(i);
      }
  
    public native @ByRef Stats stats(); public native Metadata stats(Stats setter);
  }

  // Create a reader that will return log records from "*file".
  // "*file" must remain live while this Reader is in use.
  public RecordReader(
        RandomAccessFile file,
        @Const @ByRef(nullValue = "tensorflow::io::RecordReaderOptions()") RecordReaderOptions options) { super((Pointer)null); allocate(file, options); }
  private native void allocate(
        RandomAccessFile file,
        @Const @ByRef(nullValue = "tensorflow::io::RecordReaderOptions()") RecordReaderOptions options);
  public RecordReader(
        RandomAccessFile file) { super((Pointer)null); allocate(file); }
  private native void allocate(
        RandomAccessFile file);

  // Read the record at "*offset" into *record and update *offset to
  // point to the offset of the next record.  Returns OK on success,
  // OUT_OF_RANGE for end of file, or something else for an error.
  public native @ByVal Status ReadRecord(@Cast("tensorflow::uint64*") LongPointer offset, @StdString @Cast({"char*", "std::string*"}) BytePointer record);
  public native @ByVal Status ReadRecord(@Cast("tensorflow::uint64*") LongBuffer offset, @StdString @Cast({"char*", "std::string*"}) BytePointer record);
  public native @ByVal Status ReadRecord(@Cast("tensorflow::uint64*") long[] offset, @StdString @Cast({"char*", "std::string*"}) BytePointer record);

  // Return the metadata of the Record file.
  //
  // The current implementation scans the file to completion,
  // skipping over the data regions, to extract the metadata once
  // on the first call to GetStats().  An improved implementation
  // would change RecordWriter to write the metadata into TFRecord
  // so that GetMetadata() could be a const method.
  //
  // 'metadata' must not be nullptr.
  public native @ByVal Status GetMetadata(Metadata md);
}
