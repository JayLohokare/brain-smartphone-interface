# brain-smartphone-interface

Smartphone control using BCI.

Uses OpenBCI Ganglion board for BCI unit.

The data transfer from BCI processor to Android is achieved by BLE (https://github.com/Brain-Hero/GanglionAndroidBluetooth).

We implemented the interface using Android's Accessibility features. The app overlays numbers over all possible touch-inputs possible on the phone. The number the person thinks will be detected and sent over to Android via BLE. The app then performs a click event where the number is placed.

The system we have developed enables a smartphone control interface which has never been possible before. We plan to add a language model in our app to allow auto-completed texts, thereby achieving best in class text input speeds using BCI.
