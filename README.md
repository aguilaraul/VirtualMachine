# VirtualMachine
 Converts VM commands into HACK assembly code

 #### Background
 Before a high-level program can run on a target computer, it must be translated
 into the computer's machine language. Normally, a separate compiler is written
 specifically for any given pair of high-level language and target machine
 language. One way to decouple this dependency is to break the overall
 compilation process into two nearly separate stages. In the first stage, the
 high-level program is parsed and its commands are translated into intermediate
 processing steps -- steps that are neither "high" nor "low". In the second
 stage, the intermediate steps are translated further into the machine language
 of the target hardware.

 Specifically, once can formulate a _virtual machine_ whose instructions are the
 intermediate processing steps into which high-level commands are decomposed.
 The compiler that was formerly a single monolithic program is now split into
 two separate programs. The first program, still termed _compiler_, translates the
 high-level code into intermediate VM instructions, while the second program
 translates this VM code into the machine language of the target platform.

 #### VM Specification, Part I
 The virtual machine is _stack-based_: all operations are done on a stack. It is
 also _function-based_: a complete VM program is organized in program units
 called _functions_, written in the VM language. Each function has its own
 stand-alone code and is separately handled. The VM language has a single 16-bit
 data type that can be used an integer, a Boolean, or a pointer. The language
 consists of four types of commands:

 * _Arithmetic commands_ perform arithmetic and logical operations on the stack
 * _Memory access commands_ transfer data between the stack and virtual memory
 segments.
 * _Program flow commands_ facilitate conditional and unconditional branching
 operations
 * _Function calling commands_ call functions and return from them.

 ##### Program and Command Structure
 A VM _program_ is a collection of one or more _files_ with a .vm extension,
 each consisting of one or more _functions_. From a compilation standpoint,
 these constructs correspond, respectively, to the notions of _program_, _class_,
 and _method_ in an object-oriented language.

 Within a .vm file, each VM command appears in a separate line, and in one of
 the following formats: _command_ (e.g., add), _command arg_ (e.g., goto loop),
 or _command arg1 arg2_ (e.g., push local 3). The arguments are separated from
 each other and from the _command_ part by one or more spaces. "//" comments can
 appear at the end of any line and are ignored. Blank lines are permitted and
 ignored.
