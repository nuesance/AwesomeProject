/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 */

import React from 'react';
import type {PropsWithChildren} from 'react';
import {
  Button,
  SafeAreaView,
  ScrollView,
  StatusBar,
  StyleSheet,
  useColorScheme,
  View,
} from 'react-native';
import {NativeModules} from 'react-native';

const {WallpaperModule} = NativeModules;

function App(): JSX.Element {
  const isDarkMode = useColorScheme() === 'dark';
  const onPress = str => {
    // CalendarModule.createCalendarEvent('testName', 'testLocation');
    WallpaperModule.setWallpaper(str);
  };

  return (
    <SafeAreaView>
      <StatusBar barStyle={isDarkMode ? 'light-content' : 'dark-content'} />
      <ScrollView contentInsetAdjustmentBehavior="automatic">
        <View>
          <Button
            title="wallpaper 1"
            onPress={() => {
              onPress('1');
            }}
          />
          <Button
            title="wallpaper 2"
            onPress={() => {
              onPress('2');
            }}
          />
        </View>
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  sectionContainer: {
    marginTop: 32,
    paddingHorizontal: 24,
  },
  sectionTitle: {
    fontSize: 24,
    fontWeight: '600',
  },
  sectionDescription: {
    marginTop: 8,
    fontSize: 18,
    fontWeight: '400',
  },
  highlight: {
    fontWeight: '700',
  },
});

export default App;
