/* "gorela":
        MD5:        7de2c417fbf28db91285f5d047132c29
        SHA-256:    69eabef5fa8845340ad9fdec13dbc1485b10b760c1e580d8a81d33c4f807246b
    "kljen"
        MD5:        84e3bc8f2edc71abb4e22e1163e921c9
        SHA-256:    84272a03a83349e39109d3fdb12cfbc746ffdc6968a180145a6dc7653f15cc63
    "klen"
        MD5:        7bb42c3cc3e0a5cd245c203d8c02d3cb
        SHA-256:    0ff08c272f461bbfd05e83ca25ae4729ef818e0a74d7c1c13070ff3cbec0e08a
* */
public class Main {

    public static void main(String[] args) {

        // maybe a hash generator class -> add a "Generate random hash" button in GUI

        int input = 0;

        switch(input) {
            case 0:
                seqGUI.GUI();
                break;
            case 1:
                parGUI.GUI();
                break;
            case 2:
                // CUDA
                break;
            case 3:
                Result.showResult("69.7s", "Banana");
                break;
            default:

        }

    }
}