## TVL parser


To run the parser, run the command ```gradle run --args="PATH TO INPUT FILE"```. 

If you need to parse several files with input data, then their paths must be separated by a space: ```gradle run --args="C:\data\firstFile.hex C:\data\secondFile.hex"```                      
Input file should contains data which need to parse as HEX string. Output example:

```  TLV #1
  Tag (class: U, kind: C, id: 16) [30]
  Length: 7 [07]
  Value: (1 TLVs)
    TLV #1
    Tag (class: C, kind: C, id: 0) [A0]
    Length: 5 [05]
    Value: (1 TLVs)
      TLV #1
      Tag (class: U, kind: C, id: 16) [30]
      Length: 3 [03]
      Value: (1 TLVs)
        TLV #1
        Tag (class: C, kind: P, id: 0) [80]
        Length: 1 [01]
        Value: [00]                                                                                                                                      10:01  TLV #2
  Tag (class: U, kind: C, id: 16) [30]
  Length: 15 [0F]
  Value: (2 TLVs)
    TLV #1
    Tag (class: C, kind: C, id: 0) [A0]
    Length: 11 [0B]
    Value: (1 TLVs)
      TLV #1
      Tag (class: U, kind: C, id: 16) [30]
      Length: 9 [09]
      Value: (3 TLVs)
        TLV #1
        Tag (class: C, kind: P, id: 0) [80]
        Length: 1 [01]
        Value: [07]
        TLV #2
        Tag (class: C, kind: P, id: 1) [81]
        Length: 1 [01]
        Value: [0D]
        TLV #3
        Tag (class: C, kind: P, id: 3) [83]
        Length: 1 [01]
        Value: [09]
    TLV #2
    Tag (class: C, kind: P, id: 1) [81]
    Length: 0 [00]
    Value: []```
